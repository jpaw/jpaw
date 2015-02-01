package de.jpaw.primitivecollections;

import java.util.Arrays;

/** reverse HashMap to return a primitive long (key) for an Object type key. */
public class HashMapObjectPrimitiveLong<K> extends AbstractHashMap {
    
    static class Entry<K>{
        final K key;
        long value;
        Entry<K> next;

        public Entry(K key) {
            this.key = key;
        }
    }
    /*
     * The internal data structure to hold Entries
     */
    transient Entry<K>[] elementData;
    /*
     * maximum ratio of (stored elements)/(storage size) which does not lead to
     * rehash
     */
    final float loadFactor;

    /**
     * Create a new element array
     *
     * @param s
     * @return Reference to the element array
     */
    @SuppressWarnings("unchecked")
    Entry<K>[] newElementArray(int s) {
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
    public HashMapObjectPrimitiveLong(int capacity) {
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
    public HashMapObjectPrimitiveLong(int capacity, float loadFactor) {
        if (capacity >= 0 && loadFactor > 0) {
            capacity = calculateCapacity(capacity);
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
            modCount++;
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
    public long get(K key) {
        Entry<K> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return 0;
    }

    final Entry<K> getEntry(K key) {
        int hash = key.hashCode();
        int index = hash & (elementData.length - 1);
        return findNonNullKeyEntry(key, index, hash);
    }

    final Entry<K> findNonNullKeyEntry(K key, int index, int keyHash) {
        Entry<K> m = elementData[index];
        while (m != null && key != m.key) {
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
    public long put(K key, long value) {
        Entry<K> entry;
        int hash = key.hashCode();
        int index = hash & (elementData.length - 1);
        entry = findNonNullKeyEntry(key, index, hash);
        if (entry == null) {
           modCount++;
           entry = createHashedEntry(key, index);
           if (++elementCount > threshold) {
               rehash();
           }
        }

        long result = entry.value;
        entry.value = value;
        return result;
    }


    Entry<K> createHashedEntry(K key, int index) {
        Entry<K> entry = new Entry<K>(key);
        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }
    
    void rehash(int capacity) {
        int length = calculateCapacity((capacity == 0 ? 1 : capacity << 1));

        Entry<K>[] newData = newElementArray(length);
        for (int i = 0; i < elementData.length; i++) {
            Entry<K> entry = elementData[i];
            elementData[i] = null;
            while (entry != null) {
                int index = entry.key.hashCode() & (length - 1);
                Entry<K> next = entry.next;
                entry.next = newData[index];
                newData[index] = entry;
                entry = next;
            }
        }
        elementData = newData;
        computeThreshold();
    }

    void rehash() {
        rehash(elementData.length);
    }
    public long remove(K key) {
        Entry<K> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return 0;
    }


    final Entry<K> removeEntry(K key) {
        int index = 0;
        Entry<K> entry;
        Entry<K> last = null;

        int hash = key.hashCode();
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
        modCount++;
        elementCount--;
        return entry;
    }
}
