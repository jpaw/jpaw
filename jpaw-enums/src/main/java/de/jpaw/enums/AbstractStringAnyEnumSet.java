package de.jpaw.enums;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

// test cases for the set operations see project bonaparte-core-test, class TestStringEnumSetOperations and TestStringXEnumSetOperations
public abstract class AbstractStringAnyEnumSet<E> extends AbstractCollection<E> implements Set<E>, Serializable, EnumSetMarker {
    private static final long serialVersionUID = 3439839139170000L + 94;
    private String bitmap;
    protected final static String EMPTY = "";

    // allow to make the set immutable
    private transient boolean _was$Frozen = false;      // current state of this instance

    public final boolean was$Frozen() {
        return _was$Frozen;
    }
    protected final void verify$Not$Frozen() {
        if (_was$Frozen)
            throw new RuntimeException("Setter called for frozen instance of class " + getClass().getName());
    }
    public void freeze() {
        _was$Frozen = true;
    }

    // not required for String type, but defined for consistency
    abstract protected int getMaxOrdinal();

    public static final boolean isSorted(String s) {
        final int n = s.length();
        if (n >= 2) {
            char c = s.charAt(0);
            for (int i = 1; i < n; ++i) {
                char d = s.charAt(i);
                if (c >= d)
                    return false;
                c = d;
            }
        }
        return true;
    }
    
    public static final String sortTokens(String s) {
        if (s.length() < 2)
            return s;
        char[] charArray = s.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
    
    protected AbstractStringAnyEnumSet(String bitmap) {
        this.bitmap = isSorted(bitmap) ? bitmap : sortTokens(bitmap);
    }


    // verifies that the bitmap contains characters in the correct sequence
    public void validate() {
        if (!isSorted(bitmap))
            throw new RuntimeException("Unsorted EnumSet: " + bitmap);
    }
    
    public String getBitmap() {
        return bitmap;
    }

    /** Creates a bitmap from an array of arbitrary enums. */
    public static String bitmapOf(TokenizableEnum [] arg) {
        TreeSet<String> values = new TreeSet<String>();
        for (int i = 0; i < arg.length; ++i) {
            values.add(arg[i].getToken());
        }
        StringBuilder buff = new StringBuilder(values.size());
        for (String token : values)
            buff.append(token);
        return buff.toString();
    }

    @Override
    public int size() {
        return bitmap.length();
    }

    @Override
    public boolean isEmpty() {
        return bitmap.length() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null || !(o instanceof Enum))
            return false;
        return contains(((TokenizableEnum)o).getToken());
    }

    // Override this to implement scenarios where tests must align at multiples of the token size
    protected boolean contains(String token) {
        return bitmap.contains(token);
    }

    // add the token, which is known not to exist in the bitmap
    protected void add(String token) {
        final char c = token.charAt(0);
        for (int pos = 0; pos < bitmap.length(); ++pos) {
            // perform the Java equivalent of C's strncmp, without GC overhead (extracting substrings)
            if (bitmap.charAt(pos) > c) {
                // insert it before here
                bitmap = pos == 0 ? token + bitmap : bitmap.substring(0, pos) + token + bitmap.substring(pos);
                return;
            }
        }
        // at end: append it!
        bitmap = bitmap + token;
    }

    // Utility method to add a component enum
    public boolean addEnum(TokenizableEnum e) {
        String token = e.getToken();    // may throw NPE, as per contract
        verify$Not$Frozen();            // check if modification is allowed
        if (bitmap.length() == 0) {
            // shortcut for a simple case
            bitmap = token;
            return true;
        }
        if (contains(token))
            return false;               // was already present
        // find it: skip any tokens before. Assumption is that linear search is effectively faster than bisection here due to small sizes
        add(token);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        String token = ((TokenizableEnum)o).getToken();
        verify$Not$Frozen();            // check if modification is allowed
        if (bitmap.equals(token)) {
            // shortcut, removing the last element: avoid substrings
            bitmap = EMPTY;
            return true;
        }
        final char c = token.charAt(0);
        int index = bitmap.indexOf(c);
        if (index < 0)
            return false;
        bitmap = index == 0 ? bitmap.substring(1) : bitmap.substring(0, index) + bitmap.substring(index + 1);
        return true;
    }

    @Override
    public void clear() {
        verify$Not$Frozen();            // check if modification is allowed
        bitmap = EMPTY;
    }

    @Override
    public int hashCode() {
        return bitmap.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap.equals(((AbstractStringAnyEnumSet<?>)o).getBitmap());
    }


    /** Merges (boolean OR) another bitmap into this one. */
    public void unifyWith(AbstractStringAnyEnumSet<E> that) {
        unifyWith(that.bitmap);
    }
    
    /** Merges (boolean OR) another bitmap into this one. (String parameter) */
    public void unifyWith(String that) {
        verify$Not$Frozen();                // check if modification is allowed
        final int m = that.length();
        if (m == 0) {
            return; // no op
        }
        final int n = bitmap.length();
        if (n == 0) {
            bitmap = that;
            return; // no op
        }
        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n + m); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = that.charAt(j);
            if (c < d) {
                buff.append(c);
                ++i;
            } else {
                buff.append(d);
                ++j;
                if (c == d) {
                    // common char, skip both!
                    ++i;
                }
            }
        }
        if (i < n) {
            // append the rest of this
            buff.append(bitmap.substring(i));
        } else if (j < m) {
            // append the rest of that
            buff.append(that.substring(j));
        }
        bitmap = buff.toString();
    }

    /** Merges (boolean AND) another bitmap into this one. */
    public void intersectWith(AbstractStringAnyEnumSet<E> that) {
        intersectWith(that.bitmap);
    }

    /** Merges (boolean AND) another bitmap into this one. (String parameter) */
    public void intersectWith(String that) {
        verify$Not$Frozen();                // check if modification is allowed
        final int n = bitmap.length();
        if (n == 0) {
            return; // no op
        }
        final int m = that.length();
        if (m == 0) {
            bitmap = that;                  // will be empty as well now
            return;
        }
        
        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n); // worst case length: cannot be longer than before
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = that.charAt(j);
            if (c < d) {
                ++i;        // remove token, not in that
            } else {
                if (c == d) {
                    // common char!
                    buff.append(c);
                    ++i;
                }
                ++j;
            }
        }
        bitmap = buff.toString();
    }

    /** Subtracts another bitmap from this one. */
    public void exclude(AbstractStringAnyEnumSet<E> that) {
        exclude(that.bitmap);
    }
    
    /** Subtracts another bitmap from this one. (String parameter) */
    public void exclude(String that) {
        verify$Not$Frozen();                // check if modification is allowed
        final int m = that.length();
        if (m == 0) {
            return; // no op
        }
        final int n = bitmap.length();
        if (n == 0) {
            return; // no op
        }
        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = that.charAt(j);
            if (c < d) {
                buff.append(c);
                ++i;
            } else {
                ++j;
                if (c == d) {
                    // common char, skip both!
                    ++i;
                }
            }
        }
        if (i < n) {
            // append the rest of this
            buff.append(bitmap.substring(i));
        }
        bitmap = buff.toString();
    }

    /** flips the bits of another bitmap in this one (xor). */
    public void flip(AbstractStringAnyEnumSet<E> that) {
        flip(that.bitmap);
    }
    
    /** flips the bits of another bitmap in this one (xor). (String parameter) */
    public void flip(String that) {
        final int m = that.length();
        if (m == 0)
            return;
        final int n = bitmap.length();
        if (n == 0) {
            bitmap = that;
            return;
        }
        // the empty set is returned only if both are identical
        if (bitmap.equals(that)) {
            bitmap = EMPTY;
            return;
        }
        // real compare, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n + m); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = that.charAt(j);
            if (c < d) {
                buff.append(c);
                ++i;
            } else {
                ++j;
                if (c == d) {
                    // common char, skip both!
                    ++i;
                } else {
                    buff.append(d);
                }
            }
        }
        if (i < n) {
            // append the rest of this
            buff.append(bitmap.substring(i));
        } else if (j < m) {
            // append the rest of that
            buff.append(that.substring(j));
        }
        bitmap = buff.toString();
    }
}
