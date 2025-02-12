package de.jpaw.enums;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** An alternate implementation of EnumSet, which stores the contained elements as a component inside Strings.
 * The class is not thread safe. Implementations which intend to perform parallel modification must use external locking mechanisms.
 * Further requirements of this implementation:
 * All tokens must be of length 1. */
public abstract class AbstractStringXEnumSet<E extends AbstractXEnumBase<E>> extends AbstractStringAnyEnumSet<E> implements Serializable {
    private static final long serialVersionUID = 34398390989170000L + 98;

    protected AbstractStringXEnumSet() {
        super(EMPTY);
    }

    protected AbstractStringXEnumSet(final String bitmap) {
        super(bitmap);
    }

    @Override
    public boolean add(final E e) {
        return addEnum(e);
    }

    @Override
    public boolean contains(final Object o) {
        if (o != null && o instanceof AbstractXEnumBase)
            return contains(((AbstractXEnumBase)o).getToken());
        return super.contains(o);
    }


    /** Iterator which returns the elements of the set in order of tokens sorted ascending. */
    static protected class SetOfXEnumsIterator<E extends AbstractXEnumBase<E>> implements Iterator<E> {
        private final XEnumFactory<E> myFactory;
        private final String bitmap;
        private int index = 0;

        public SetOfXEnumsIterator(final String bitmap, final XEnumFactory<E> myFactory) {
            this.myFactory = myFactory;
            this.bitmap = bitmap;
        }

        @Override
        public boolean hasNext() {
            return index < bitmap.length();
        }

        @Override
        public E next() {
            if (bitmap.length() <= index) {
                // by contract, NoSuchElement exception should be thrown
                throw new NoSuchElementException();
            }
            ++index;
            final E nextEnum = myFactory.getByToken(bitmap.substring(index-1, index));
            if (nextEnum == null) {
                throw new NoSuchElementException();
            }
            return nextEnum;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
