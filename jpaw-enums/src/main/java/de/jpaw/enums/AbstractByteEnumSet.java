package de.jpaw.enums;

import java.util.Iterator;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is a byte, therefore the maximum number of enum tokens is 7 (as we don't want negative values). */
public abstract class AbstractByteEnumSet<E extends Enum<E>> extends AbstractFreezableEnumSet<E> {
    private static final long serialVersionUID = 34398390989170000L + 7;
    private static final byte BIT = 1;
    /** The maximum number of tokens this enumset can store. */
    public static final int MAX_TOKENS = 7;
    private byte bitmap;

    /** This method returns the number of instances (max Ordinal + 1), the name is misleading!!!!
     * This definition has been made to avoid a negative return code in the case of empty enums. */
    protected abstract int getMaxOrdinal();

    public AbstractByteEnumSet() {
        bitmap = 0;
    }

    public AbstractByteEnumSet(final byte bitmap) {
        this.bitmap = bitmap;
    }


    public final byte getBitmap() {
        return bitmap;
    }

    /** Constructs a String with one digit / letter representing a bit position. */
    public String asStringMap() {
        final StringBuilder sb = new StringBuilder(MAX_TOKENS);
        byte rotmap = bitmap;
        for (int i = 0; i < MAX_TOKENS; ++i) {
            if ((rotmap & BIT) != 0)
                sb.append(STANDARD_TOKENS.charAt(i));
            rotmap >>= 1;
        }
        return sb.toString();
    }

    /** Constructs a bitmap from a standard string map. */
    public static byte fromStringMap(final String s) {
        byte work = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            final int pos = STANDARD_TOKENS.indexOf(c);
            if (pos >= 0 && pos < MAX_TOKENS) {
                work |= BIT << pos;
            } else {
                throw new IllegalArgumentException("Invalid enum set character: " + Character.valueOf(c));
            }
        }
        return work;
    }

    /** Creates a bitmap from an array of arbitrary enums. */
    public static byte bitmapOf(final Enum<?>[] arg) {
        byte val = 0;
        for (int i = 0; i < arg.length; ++i) {
            val |= BIT << arg[i].ordinal();
        }
        return val;
    }

    @Override
    public final int size() {
        return Long.bitCount(bitmap);
    }

    @Override
    public final boolean isEmpty() {
        return bitmap == 0;
    }

    @Override
    public final boolean contains(final Object o) {
        if (o == null || !(o instanceof Enum))
            return false;
        final int q = ((Enum<?>)o).ordinal();
        return q < MAX_TOKENS && (bitmap & (BIT << q)) != 0;
    }

    @Override
    public final boolean add(final E e) {
        final int q = e.ordinal();   // may throw NPE
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        final byte b = (byte) (BIT << q);
        if ((bitmap & b) != 0)
            return false;
        verify$Not$Frozen();            // check if modification is allowed
        bitmap |= b;
        return true;
    }

    @Override
    public final boolean remove(final Object o) {
        if (o == null || !(o instanceof Enum))      // preliminary check for "not contained"
            return false;
        final int q = ((Enum<?>)o).ordinal();
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(o.getClass().getCanonicalName() + "." + o.toString() + " has ordinal " + q + " which is too big for this set");
        final byte b = (byte) (BIT << q);
        if ((bitmap & b) == 0)
            return false;
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~b;
        return true;
    }

    @Override
    public final void clear() {
        if (bitmap != 0) {
            verify$Not$Frozen();            // check if modification is allowed
            bitmap = 0;
        }
    }

    @Override
    public final int hashCode() {
        return bitmap;
    }

    @Override
    public final boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap == ((AbstractByteEnumSet<?>)o).getBitmap();
    }

    /** Merges (boolean OR) another bitmap into this one. */
    public final void unifyWith(final AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap |= that.bitmap;
    }

    /** Merges (boolean AND) another bitmap into this one. */
    public final void intersectWith(final AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= that.bitmap;
    }

    /** Subtracts another bitmap from this one. */
    public final void exclude(final AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~that.bitmap;
    }

    /** Merges (XOR) another bitmap into this one. Provided for completeness */
    public final void exactlyOneOf(final AbstractByteEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap ^= that.bitmap;
    }

    /** Returns the bitmap of the full set with all elements included. */
    public final byte bitmapFullSet() {
        return (byte) ((BIT << getMaxOrdinal()) - BIT);      // Java looses type on binary operations such that a cast is required for byte and byte
    }

    /** Negates a set. */
    public final void complement() {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap = (byte) (~bitmap & bitmapFullSet());      // Java looses type on binary operations such that a cast is required for byte and byte
    }

    protected static final class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final E[] values;
        private byte bitmap;
        private int index;

        public SetOfEnumsIterator(final E[] values, final byte bitmap) {
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
            while ((bitmap & (BIT << index)) == 0) {
                ++index;
            }
            bitmap &= ~(BIT << index);
            return values[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
