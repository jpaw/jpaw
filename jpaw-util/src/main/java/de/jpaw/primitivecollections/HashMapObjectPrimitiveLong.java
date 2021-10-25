package de.jpaw.primitivecollections;

import java.util.Arrays;

import de.jpaw.util.IntegralLimits;

/** reverse HashMap to return a primitive long (key) for an Object type key. */
public final class HashMapObjectPrimitiveLong<K> {

    private static class Entry<K> {
        final K key;
        long value;
        Entry<K> next;

        Entry(final K key) {
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
    private Entry<K>[] elementData;
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
    private @SuppressWarnings("unchecked")
    Entry<K>[] newElementArray(final int s) {
        return new Entry[s];
    }

    /**
     * Constructs a new empty {@code HashMap} instance.
     */
    public HashMapObjectPrimitiveLong() {
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
    public HashMapObjectPrimitiveLong(final int capacity) {
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
    public HashMapObjectPrimitiveLong(int capacity, final float loadFactor) {
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
    public long get(final K key) {
        final Entry<K> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return 0;
    }

    private Entry<K> getEntry(final K key) {
        final int hash = key.hashCode();
        final int index = hash & (elementData.length - 1);
        return findNonNullKeyEntry(key, index, hash);
    }

    private Entry<K> findNonNullKeyEntry(final K key, final int index, final int keyHash) {
        Entry<K> m = elementData[index];
        while (m != null && !key.equals(m.key)) {
            m = m.next;
        }
        return m;
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
    public long put(final K key, final long value) {
        Entry<K> entry;
        final int hash = key.hashCode();
        final int index = hash & (elementData.length - 1);
        entry = findNonNullKeyEntry(key, index, hash);
        if (entry == null) {
           entry = createHashedEntry(key, index);
           if (++elementCount > threshold) {
               rehash();
           }
        }

        final long result = entry.value;
        entry.value = value;
        return result;
    }


    private Entry<K> createHashedEntry(final K key, final int index) {
        final Entry<K> entry = new Entry<>(key);
        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }

    private void rehash(final int capacity) {
        final int length = capacity < DEFAULT_SIZE ? DEFAULT_SIZE : capacity << 1;

        final Entry<K>[] newData = newElementArray(length);
        for (int i = 0; i < elementData.length; i++) {
            Entry<K> entry = elementData[i];
            elementData[i] = null;
            while (entry != null) {
                final int index = entry.key.hashCode() & (length - 1);
                final Entry<K> next = entry.next;
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
    public long remove(final K key) {
        final Entry<K> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return 0;
    }


    private Entry<K> removeEntry(final K key) {
        int index = 0;
        Entry<K> entry;
        Entry<K> last = null;

        final int hash = key.hashCode();
        index = hash & (elementData.length - 1);
        entry = elementData[index];
        while (entry != null && !key.equals(entry.key)) {
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
