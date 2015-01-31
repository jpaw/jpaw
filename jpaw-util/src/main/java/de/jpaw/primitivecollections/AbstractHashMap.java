package de.jpaw.primitivecollections;

import java.util.Random;

public class AbstractHashMap {

    /*
     * Actual count of entries
     */
    transient int elementCount;

    /*
     * modification count, to keep track of structural modifications between the HashMap and the iterator
     */
    transient int modCount = 0;

    /*
     * default size that an HashMap created using the default constructor would have.
     */
    protected static final int DEFAULT_SIZE = 16;

    /*
     * maximum number of elements that can be put in this map before having to rehash
     */
    int threshold;

    /**
     * Salt added to keys before hashing, so it is harder to trigger hash collision attack.
     */
    protected final long hashSalt = hashSaltValue();

    protected long hashSaltValue() {
        return new Random().nextLong();
    }

    /**
     * Calculates the capacity of storage required for storing given number of elements. The result is a power of 2.
     *
     * @param x
     *            number of elements
     * @return storage size
     */
    protected static int calculateCapacity(int x) {
        if (x >= 1 << 30) {
            return 1 << 30;
        }
        if (x <= 16) {
            return 16;
        }
        x = x - 1;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    /**
     * Returns the number of elements in this map.
     *
     * @return the number of elements in this map.
     */
    public int size() {
        return elementCount;
    }

}
