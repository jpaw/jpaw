package de.jpaw.enums;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/** An alternate implementation of EnumSet, which stores the contained elements as a component inside Strings.
 * The class is not thread safe. Implementations which intend to perform parallel modification must use external locking mechanisms.
 * Further requirements of this implementation:
 * All tokens must be of length 1. */
public abstract class AbstractStringEnumSet<E extends TokenizableEnum> extends AbstractCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 34398390989170000L + 99;
    private String bitmap;
    
    // allow to make the set immutable
    private transient boolean _is$Frozen = false;      // current state of this instance

    public final boolean is$Frozen() {
        return _is$Frozen;
    }
    protected final void verify$Not$Frozen() {
        if (_is$Frozen)
            throw new RuntimeException("Setter called for frozen instance of class " + getClass().getName());
    }
    public void freeze() {
        _is$Frozen = true;
    }
    
    // not required for String type, but defined for consistency
    abstract protected int getMaxOrdinal();
    
    public AbstractStringEnumSet() {
        bitmap = "";
    }
    
    public AbstractStringEnumSet(String bitmap) {
        this.bitmap = bitmap;
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
    
    @Override
    public boolean add(E e) {
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
            bitmap = "";
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
        bitmap = "";
    }
    
    @Override
    public int hashCode() {
        return bitmap.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap == ((AbstractStringEnumSet<?>)o).getBitmap();
    }
    
    /** Iterator which returns the elements of the set in order of tokens sorted ascending. */
    static protected class SetOfEnumsIterator<E extends TokenizableEnum> implements Iterator<E> {
        private final String bitmap;
        private int index = 0;
        static private final ConcurrentHashMap<String, TokenizableEnum> lookupTable = new ConcurrentHashMap<String, TokenizableEnum>();
        
        public SetOfEnumsIterator(E [] values, String bitmap) {
            this.bitmap = bitmap;
            if (lookupTable.size() < values.length) {
                // hashmap not up to date. fill it. Possible duplicate fills are accepted, they perform logically correct, with just a small performance overhead
                for (E z : values)
                    lookupTable.putIfAbsent(z.getToken(), z);
            }
        }
        
        @Override
        public boolean hasNext() {
            return index < bitmap.length();
        }

        @Override
        public E next() {
            if (bitmap.length() <= index)
                return null;                // shortcut
            ++index;
            @SuppressWarnings("unchecked")
            E data = (E) lookupTable.get(bitmap.substring(index-1, index));   // GC overhead due to new String. But a Character would be as well... 
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
