package de.jpaw.enums;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is a int, therefore the maximum number of enum tokens is 31 (as we don't want negative values). */
public abstract class AbstractIntEnumSet<E extends Enum<E>> extends AbstractCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 34398390989170000L + 31;
    private static final int BIT = 1;
    public static final int MAX_TOKENS = 31;
    private int bitmap;
    
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
    
    abstract protected int getMaxOrdinal();
    
    public AbstractIntEnumSet() {
        bitmap = 0;
    }
    
    public AbstractIntEnumSet(int bitmap) {
        this.bitmap = bitmap;
    }
    
    
    public int getBitmap() {
        return bitmap;
    }
    
    /** Creates a bitmap from an array of arbitrary enums. */
    public static int bitmapOf(Enum<?> [] arg) {
        int val = 0;
        for (int i = 0; i < arg.length; ++i)
            val |= BIT << arg[i].ordinal();   
        return val;
    }
    
    @Override
    public int size() {
        return Long.bitCount(bitmap);
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
        return q < MAX_TOKENS && (bitmap & (BIT << q)) != 0;
    }
    
    @Override
    public boolean add(E e) {
        int q = e.ordinal();   // may throw NPE
        if (q >= MAX_TOKENS || q > getMaxOrdinal())
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        int b = (int) (BIT << q);
        if ((bitmap & b) != 0)
            return false;
        verify$Not$Frozen();			// check if modification is allowed
        bitmap |= b;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        E e = (E)o;
        int q = e.ordinal();   // may throw NPE
        if (q >= MAX_TOKENS || q > getMaxOrdinal())
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        int b = (int) (BIT << q);
        if ((bitmap & b) == 0)
            return false;
        verify$Not$Frozen();			// check if modification is allowed
        bitmap &= ~b;
        return true;
    }

    @Override
    public void clear() {
    	if (bitmap != 0) {
    		verify$Not$Frozen();			// check if modification is allowed
    		bitmap = 0;
    	}
    }
    
    @Override
    public int hashCode() {
    	return bitmap;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o == null || getClass() != o.getClass())
    		return false;
    	return bitmap == ((AbstractIntEnumSet<?>)o).getBitmap();
    }
    
    static protected class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final E [] values;
        private int bitmap;
        private int index;
        
        public SetOfEnumsIterator(E [] values, int bitmap) {
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
            while ((bitmap & (BIT << index)) == 0)
                ++index;
            bitmap &= ~(BIT << index);
            return values[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
