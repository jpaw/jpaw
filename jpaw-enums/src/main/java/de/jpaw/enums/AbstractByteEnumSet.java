package de.jpaw.enums;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is a byte, therefore the maximum number of enum tokens is 7 (as we don't want negative values). */
public abstract class AbstractByteEnumSet<E extends Enum<E>> extends AbstractCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 34398390989170000L + 7;
    private static final byte BIT = 1;
    public static final int MAX_TOKENS = 7;
    private byte bitmap;

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

    /** This method returns the number of instances (max Ordinal + 1), the name is misleading!!!!
     * This definition has been made to avoid a negative return code in the case of empty enums. */
    abstract protected int getMaxOrdinal();

    public AbstractByteEnumSet() {
        bitmap = 0;
    }

    public AbstractByteEnumSet(byte bitmap) {
        this.bitmap = bitmap;
    }


    public byte getBitmap() {
        return bitmap;
    }

    /** Creates a bitmap from an array of arbitrary enums. */
    public static byte bitmapOf(Enum<?> [] arg) {
        byte val = 0;
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
        byte b = (byte) (BIT << q);
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
        byte b = (byte) (BIT << q);
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
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap == ((AbstractByteEnumSet<?>)o).getBitmap();
    }

    /** Merges (boolean OR) another bitmap into this one. */
    public void unifyWith(AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap |= that.bitmap;
    }

    /** Merges (boolean AND) another bitmap into this one. */
    public void intersectWith(AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= that.bitmap;
    }

    /** Subtracts another bitmap from this one. */
    public void exclude(AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~that.bitmap;
    }

    /** Merges (XOR) another bitmap into this one. Provided for completeness */
    public void exactlyOneOf(AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap ^= that.bitmap;
    }

    /** Returns the bitmap of the full set with all elements included. */
    public byte bitmapFullSet() {
        return (byte) ((BIT << getMaxOrdinal()) - BIT);      // Java looses type on binary operations such that a cast is required for byte and byte
    }

    /** Negates a set. */
    public void complement() {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap = (byte) (~bitmap & bitmapFullSet());      // Java looses type on binary operations such that a cast is required for byte and byte
    }

    static protected class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final E [] values;
        private byte bitmap;
        private int index;

        public SetOfEnumsIterator(E [] values, byte bitmap) {
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
