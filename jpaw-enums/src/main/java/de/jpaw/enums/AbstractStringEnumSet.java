package de.jpaw.enums;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/** An alternate implementation of EnumSet, which stores the contained elements as a component inside Strings.
 * The class is not thread safe. Implementations which intend to perform parallel modification must use external locking mechanisms.
 * Further requirements of this implementation:
 * All tokens must be of length 1. */
public abstract class AbstractStringEnumSet<E extends Enum<E> & TokenizableEnum> extends AbstractStringAnyEnumSet<E> implements GenericEnumSetMarker<E> {
    private static final long serialVersionUID = 34398390989170000L + 99;

    protected AbstractStringEnumSet() {
        super(EMPTY);
    }

    protected AbstractStringEnumSet(String bitmap) {
        super(bitmap);
    }

    @Override
    public boolean add(E e) {
        return addEnum(e);
    }

    /** Let this instance have the same contents as that. */
    @Override
    public void assign(Collection<E> that) {
        clear();
        if (that != null) {
            for (E o : that)
                add(o);
        }
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
