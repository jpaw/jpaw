package de.jpaw.enums;

import java.util.Collection;
import java.util.Iterator;

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
        // static private final ConcurrentHashMap<String, TokenizableEnum> lookupTable = new ConcurrentHashMap<String, TokenizableEnum>();
        private final E [] values;
        private int index = 0;

        public SetOfEnumsIterator(E [] values, String bitmap) {
            this.bitmap = bitmap;
            this.values = values;
        }

        private E getValue(String token) {
            for (E e: values) {
                if (token.equals(e.getToken())) {
                    return e;
                }
            }
            return null;
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
            return getValue(bitmap.substring(index-1, index));   // GC overhead due to new String. But a Character would be as well...
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
