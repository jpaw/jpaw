package de.jpaw.enums;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is a long, therefore the maximum number of enum tokens is 63 (as we don't want negative values). */
public abstract class AbstractLongEnumSet<E extends Enum<E>> extends AbstractCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 34398390989170000L + 63;
    private static final long BIT = 1;
    public static final int MAX_TOKENS = 63;
    private long bitmap;

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

    /** This method returns the number of instances (max Ordinal + 1), the name is misleading!!!!
     * This definition has been made to avoid a negative return code in the case of empty enums. */
    abstract protected int getMaxOrdinal();

    public AbstractLongEnumSet() {
        bitmap = 0;
    }

    public AbstractLongEnumSet(long bitmap) {
        this.bitmap = bitmap;
    }


    public long getBitmap() {
        return bitmap;
    }

    /** Creates a bitmap from an array of arbitrary enums. */
    public static long bitmapOf(Enum<?> [] arg) {
        long val = 0;
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
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        long b = BIT << q;
        if ((bitmap & b) != 0)
            return false;
        verify$Not$Frozen();            // check if modification is allowed
        bitmap |= b;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null || !(o instanceof Enum))      // preliminary check for "not contained"
            return false;
        int q = ((Enum<?>)o).ordinal();
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(o.getClass().getCanonicalName() + "." + o.toString() + " has ordinal " + q + " which is too big for this set");
        long b = BIT << q;
        if ((bitmap & b) == 0)
            return false;
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~b;
        return true;
    }

    @Override
    public void clear() {
        if (bitmap != 0) {
            verify$Not$Frozen();            // check if modification is allowed
            bitmap = 0;
        }
    }

    @Override
    public int hashCode() {
        return (int) (bitmap ^ (bitmap >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap == ((AbstractLongEnumSet<?>)o).getBitmap();
    }

    /** Merges (boolean OR) another bitmap into this one. */
    public void unifyWith(AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap |= that.bitmap;
    }

    /** Merges (boolean AND) another bitmap into this one. */
    public void intersectWith(AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= that.bitmap;
    }

    /** Subtracts another bitmap from this one. */
    public void exclude(AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~that.bitmap;
    }

    /** Merges (XOR) another bitmap into this one. Provided for completeness */
    public void exactlyOneOf(AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap ^= that.bitmap;
    }

    /** Returns the bitmap of the full set with all elements included. */
    public long bitmapFullSet() {
        return (BIT << getMaxOrdinal()) - BIT;      // Java looses type on binary operations such that a cast is required for byte and long
    }

    /** Negates a set. */
    public void complement() {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap = ~bitmap & bitmapFullSet();      // Java looses type on binary operations such that a cast is required for byte and long
    }

    static protected class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final E [] values;
        private long bitmap;
        private int index;

        public SetOfEnumsIterator(E [] values, long bitmap) {
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
