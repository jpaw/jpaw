package de.jpaw.primitivecollections;

import java.util.Arrays;

import de.jpaw.util.IntegralLimits;

/** An implementation of a hash map with primitive keys.
 * Similar to implementations in HPPC and MapDB.
 *
 * This implementation is a slimmed down version of the one used on MapDB 1.0.6 (all iterator code has been removed), which had its origins in Apache Harmony.
 *
 * This class is not thread-safe.
 *
 */
public final class HashMapPrimitiveLongObject<V> {

    static private class Entry<V> {
        final long key;
        V value;
        Entry<V> next;

        Entry(long key) {
            this.key = key;
        }
    }

    private int elementCount;                           // current count of entries
    private static final int DEFAULT_SIZE = 16;         // initial size for the default constructor

    private int threshold;                              // when reached, a rehash will occur

    /**
     * Returns the number of elements in this map.
     *
     * @return the number of elements in this map.
     */
    public int size() {
        return elementCount;
    }

    /*
     * The internal data structure to hold Entries
     */
    private Entry<V>[] elementData;

    /*
     * maximum ratio of (stored elements)/(storage size) which does not lead to
     * rehash
     */
    private final float loadFactor;

    /**
     * Create a new element array
     *
     * @param s
     * @return Reference to the element array
     */
    @SuppressWarnings("unchecked")
    private Entry<V>[] newElementArray(int s) {
        return new Entry[s];
    }

    /**
     * Constructs a new empty {@code HashMap} instance.
     */
    public HashMapPrimitiveLongObject() {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity.
     *
     * @param capacity
     *            the initial capacity of this hash map.
     * @throws IllegalArgumentException
     *                when the capacity is less than zero.
     */
    public HashMapPrimitiveLongObject(int capacity) {
        this(capacity, 0.75f);  // default load factor of 0.75
    }


    /**
     * Constructs a new {@code HashMap} instance with the specified capacity and
     * load factor.
     *
     * @param capacity
     *            the initial capacity of this hash map.
     * @param loadFactor
     *            the initial load factor.
     * @throws IllegalArgumentException
     *                when the capacity is less than zero or the load factor is
     *                less or equal to zero.
     */
    public HashMapPrimitiveLongObject(int capacity, float loadFactor) {
        if (capacity >= 0 && loadFactor > 0) {
            capacity = capacity <= 16 ? 16 : IntegralLimits.nextPowerOf2(capacity);
            elementCount = 0;
            elementData = newElementArray(capacity);
            this.loadFactor = loadFactor;
            computeThreshold();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Removes all mappings from this hash map, leaving it empty.
     *
     * @see #size
     */
    public void clear() {
        if (elementCount > 0) {
            elementCount = 0;
            Arrays.fill(elementData, null);
        }
    }


    /**
     * Computes the threshold for rehashing
     */
    private void computeThreshold() {
        threshold = (int) (elementData.length * loadFactor);
    }

    /**
     * Returns the value of the mapping with the specified key.
     *
     * @param key
     *            the key.
     * @return the value of the mapping with the specified key, or {@code null}
     *         if no mapping for the specified key is found.
     */
    public V get(long key) {
        Entry<V> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return null;
    }

    private Entry<V> getEntry(long key) {
        int hash = longHash(key);
        int index = hash & (elementData.length - 1);
        return findNonNullKeyEntry(key, index, hash);
    }

    private Entry<V> findNonNullKeyEntry(long key, int index, int keyHash) {
        Entry<V> m = elementData[index];
        while (m != null && key != m.key) {
            m = m.next;
        }
        return m;
    }


    public static int longHash(final long key) {
        int h = (int)(key ^ (key >>> 32));
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Maps the specified key to the specified value.
     *
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @return the value of any previous mapping with the specified key or
     *         {@code null} if there was no such mapping.
     */
    public V put(long key, V value) {
        Entry<V> entry;
        int hash = longHash(key);
        int index = hash & (elementData.length - 1);
        entry = findNonNullKeyEntry(key, index, hash);
        if (entry == null) {
           entry = createHashedEntry(key, index);
           if (++elementCount > threshold) {
               rehash();
           }
        }

        V result = entry.value;
        entry.value = value;
        return result;
    }


    private Entry<V> createHashedEntry(long key, int index) {
        Entry<V> entry = new Entry<V>(key);
        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }

    void rehash(int capacity) {
        int length = capacity < DEFAULT_SIZE ? DEFAULT_SIZE : capacity << 1;

        Entry<V>[] newData = newElementArray(length);
        for (int i = 0; i < elementData.length; i++) {
            Entry<V> entry = elementData[i];
            elementData[i] = null;
            while (entry != null) {
                int index = longHash(entry.key) & (length - 1);
                Entry<V> next = entry.next;
                entry.next = newData[index];
                newData[index] = entry;
                entry = next;
            }
        }
        elementData = newData;
        computeThreshold();
    }

    private void rehash() {
        rehash(elementData.length);
    }
    public V remove(long key) {
        Entry<V> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }


    private Entry<V> removeEntry(long key) {
        int index = 0;
        Entry<V> entry;
        Entry<V> last = null;

        int hash = longHash(key);
        index = hash & (elementData.length - 1);
        entry = elementData[index];
        while (entry != null && key != entry.key) {
             last = entry;
             entry = entry.next;
        }

        if (entry == null) {
            return null;
        }
        if (last == null) {
            elementData[index] = entry.next;
        } else {
            last.next = entry.next;
        }
        elementCount--;
        return entry;
    }

}
