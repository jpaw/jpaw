package de.jpaw.enumsets;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is an int, therefore the maximum number of enum tokens is 31 (as we don't want negative values). */
public abstract class AbstractEnumSet<E extends Enum<E>> extends AbstractCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 3439839098917393638L;
    private int bitmap;
    
    abstract protected int getMaxOrdinal();
    
    public AbstractEnumSet() {
        bitmap = 0;
    }
    
    public AbstractEnumSet(int bitmap) {
        this.bitmap = bitmap;
    }
    
    
    public int getBitmap() {
        return bitmap;
    }
    
    @Override
    public int size() {
        return Integer.bitCount(bitmap);
    }
    
    @Override
    public boolean isEmpty() {
        return bitmap == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null || !(o instanceof Enum))
            return false;
        int q = ((Enum<?>)o).ordinal();
        return q < 32 && (bitmap & (1 << q)) != 0;
    }
    
    @Override
    public boolean add(E e) {
        int q = e.ordinal();   // may throw NPE
        if (q >= 31)
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        int b = 1 << q;
        if ((bitmap & b) != 0)
            return false;
        bitmap |= b;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        E e = (E)o;
        int q = e.ordinal();   // may throw NPE
        if (q >= 31)
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        int b = 1 << q;
        if ((bitmap & b) != 0)
            return false;
        bitmap &= ~b;
        return true;
    }

    @Override
    public void clear() {
        bitmap = 0;
    }
    
    static protected class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final Class<E> enumType;
        private final E [] values;
        private int bitmap;
        private int index;
        
        public SetOfEnumsIterator(Class<E> enumType, E [] values, int bitmap) {
            this.enumType = enumType;
            this.values = values;
            this.bitmap = bitmap;
            this.index = -1;
        }
        
        @Override
        public boolean hasNext() {
            return bitmap != 0;
        }

        @Override
        public E next() {
            if (bitmap == 0)
                return null;
            ++index;                                    // index was at previous position. At least increment once
            while ((bitmap & (1 << index)) == 0)
                ++index;
            bitmap &= ~ (1 << index);
            return values[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
