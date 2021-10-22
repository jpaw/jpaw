package de.jpaw.enums;

import java.io.Serializable;

// test cases for the set operations see project bonaparte-core-test, class TestImmutableStringEnumSetOperations

/** A class which provides String represented alphanumeric enum sets, such as XEnumSet or EnumSetAlpha.
 * This class does not implement the Set interface, because that contradicts immutability.
 * This class is not strictly typed, i.e. it can hold bitmaps of different XEnumSets.
 *
 * This class is intended to be used with operator overloading in xtend.
 *
 * @author mbi
 *
 */
public final class ImmutableStringEnumSet implements Serializable {
    private static final long serialVersionUID = 3439122139170012L;
    private final String bitmap;
    public static final ImmutableStringEnumSet EMPTY = new ImmutableStringEnumSet("");

    /** Factory: Construct an enum set from a String bitmap. for empty or null, always the same instance is returned. */
    public static final ImmutableStringEnumSet of(String tokens) {
        if (tokens == null || tokens.length() == 0)
            return EMPTY;
        return new ImmutableStringEnumSet(AbstractStringAnyEnumSet.isSorted(tokens) ? tokens : AbstractStringAnyEnumSet.sortTokens(tokens));
    }
    private ImmutableStringEnumSet(String bitmap) {
        this.bitmap = bitmap;
    }

    /** Construct an enum set from an (x)enumSet. */
    public ImmutableStringEnumSet(AbstractStringAnyEnumSet<?> enumSet) {
        bitmap = enumSet.getBitmap();
    }

    private final String enumToken(TokenizableEnum myEnum) {
        String s = myEnum.getToken();
        if (s == null || s.length() != 1)
            throw new IllegalArgumentException("Token of " + myEnum.getClass().getCanonicalName() + " has not length 1");
        return s;
    }

    /** Construct a single element map. */
    public ImmutableStringEnumSet(TokenizableEnum myEnum) {
        bitmap = enumToken(myEnum);
    }

    public static ImmutableStringEnumSet ofTokens(TokenizableEnum ... enums) {
        return new ImmutableStringEnumSet(AbstractStringAnyEnumSet.bitmapOf(enums));
    }

    public String getBitmap() {
        return bitmap;
    }

    public boolean contains(final TokenizableEnum myEnum) {
        return bitmap.indexOf(enumToken(myEnum).charAt(0)) >= 0;
    }


    // acts like "contains all"
    public boolean contains(final String tokens) {
        for (int i = 0; i < tokens.length(); ++i)
            if (bitmap.indexOf(tokens.charAt(i)) < 0)
                return false;
        return true;
    }

    public boolean contains(final ImmutableStringEnumSet that) {
        return contains(that.bitmap);
    }


    private ImmutableStringEnumSet add1(String token) {
        final char c = token.charAt(0);
        for (int pos = 0; pos < bitmap.length(); ++pos) {
            // perform the Java equivalent of C's strncmp, without GC overhead (extracting substrings)
            if (bitmap.charAt(pos) > c) {
                // insert it before here
                if (pos == 0)
                    return new ImmutableStringEnumSet(token + bitmap);
                else
                    return new ImmutableStringEnumSet(bitmap.substring(0, pos) + token + bitmap.substring(pos));
            }
        }
        // at end: append it!
        return new ImmutableStringEnumSet(bitmap + token);
    }

    // add equals or
    public ImmutableStringEnumSet add(final TokenizableEnum myEnum) {
        if (contains(myEnum))
            return this;
        // must add / contruct a new enum
        if (bitmap.length() == 0)
            return new ImmutableStringEnumSet(myEnum);
        // must merge
        return add1(enumToken(myEnum));
    }

    public ImmutableStringEnumSet minus(final TokenizableEnum myEnum) {
        final char c = enumToken(myEnum).charAt(0);
        final int i = bitmap.indexOf(c);
        if (i < 0)
            return this;                // token is not part of set => result is identical to this
        final int l = bitmap.length();
        if (l == 1)
            return EMPTY;               // token is part of set and set has a single element only => must be the same then, result is empty
        // true subtract
        if (i == 0)
            return new ImmutableStringEnumSet(bitmap.substring(1));     // special case: remove first!
        if (i == l - 1)
            return new ImmutableStringEnumSet(bitmap.substring(0, i));  // special case: remove last!
        return new ImmutableStringEnumSet(bitmap.substring(0, i) + bitmap.substring(i + 1));    // remove something in between
    }

    // set operations: and (intersect), or (union), minus (without)

    /** Merges (boolean OR) another set into this one. */
    public ImmutableStringEnumSet or(final ImmutableStringEnumSet that) {
        final String thatmap = that.bitmap;
        final int m = thatmap.length();
        if (m == 0) {
            return this;
        }
        final int n = bitmap.length();
        if (n == 0) {
            return that;
        }
        if (bitmap.equals(thatmap)) // shortcut: special case, avoids creation of a new instance
            return this;

        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n + m); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = thatmap.charAt(j);
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
            buff.append(thatmap.substring(j));
        }
        return new ImmutableStringEnumSet(buff.toString());
    }

    /** Merges (boolean AND) another set into this one. */
    public ImmutableStringEnumSet and(final ImmutableStringEnumSet that) {
        final int n = bitmap.length();
        if (n == 0) {
            return this; // no op
        }
        final String thatmap = that.bitmap;
        final int m = thatmap.length();
        if (m == 0) {
            return that;
        }
        if (bitmap.equals(thatmap)) // shortcut: special case, avoids creation of a new instance
            return this;

        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n); // worst case length: cannot be longer than before
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = thatmap.charAt(j);
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
        if (buff.length() == 0)
            return EMPTY;
        return new ImmutableStringEnumSet(buff.toString());
    }

    /** Subtracts another bitmap from this one. */
    public ImmutableStringEnumSet minus(ImmutableStringEnumSet that) {
        final String thatmap = that.bitmap;
        final int m = thatmap.length();
        final int n = bitmap.length();
        if (m == 0 || n == 0) {
            return this;
        }
        if (bitmap.equals(thatmap))     // just a shortcut here, and not the only case when EMPTY is returned
            return EMPTY;
        // real merge, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = thatmap.charAt(j);
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
        if (buff.length() == 0)
            return EMPTY;
        return new ImmutableStringEnumSet(buff.toString());
    }

    /** Builds the symmetric exclusive or. */
    public ImmutableStringEnumSet xor(ImmutableStringEnumSet that) {
        final String thatmap = that.bitmap;
        final int m = thatmap.length();
        if (m == 0)
            return this;
        final int n = bitmap.length();
        if (n == 0) {
            return that;
        }
        // the empty set is returned only if both are identical
        if (bitmap.equals(thatmap))
            return EMPTY;
        // real compare, no shortcut possible. Use a linear time algorithm. We know both bitmaps are sorted.
        StringBuilder buff = new StringBuilder(n + m); // worst case length
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            // loops while both sets have characters to merge
            // the result is added either c or d
            final char c = bitmap.charAt(i);
            final char d = thatmap.charAt(j);
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
            buff.append(thatmap.substring(j));
        }
        return new ImmutableStringEnumSet(buff.toString());
    }




    public boolean isEmpty() {
        return bitmap.length() == 0;
    }

    /** Returns the number of elements. */
    public int size() {
        return bitmap.length();
    }

    @Override
    public String toString() {
        return bitmap;
    }

    @Override
    public int hashCode() {
        return bitmap.hashCode();
    }

    // two ImmutableStringEnumSet are considered equal if they have the same contents
    @Override
    public boolean equals(Object _that) {
        if (this == _that)
            return true;
        if (_that == null || getClass() != _that.getClass())
            return false;
        return bitmap.equals(((ImmutableStringEnumSet)_that).bitmap);
    }

}
