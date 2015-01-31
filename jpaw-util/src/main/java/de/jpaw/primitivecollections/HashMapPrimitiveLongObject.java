package de.jpaw.primitivecollections;

import java.util.Arrays;

/** An implementation of a hash map with primitive keys.
 * Similar to implementations in HPPC and MapDB.
 * 
 * This implementation is a slimmed down version of the one used on MapDB 1.0.6 (all iterator code has been removed), which had its origins in Apache Harmony.
 * 
 * This class is not thread-safe.
 *
 */
public class HashMapPrimitiveLongObject<V> extends AbstractHashMap {
    /*
     * The internal data structure to hold Entries
     */
    transient Entry<V>[] elementData;
    
    static class Entry<V>{
        final int origKeyHash;

        final long key;
        V value;
        Entry<V> next;

        public Entry(long key, int hash) {
            this.key = key;
            this.origKeyHash = hash;
        }
    }
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
    Entry<V>[] newElementArray(int s) {
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
    public V get(long key) {
        Entry<V> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return null;
    }

    final Entry<V> getEntry(long key) {
        int hash = HashMapPrimitiveLongObject.longHash(key^hashSalt);
        int index = hash & (elementData.length - 1);
        return findNonNullKeyEntry(key, index, hash);
    }

    final Entry<V> findNonNullKeyEntry(long key, int index, int keyHash) {
        Entry<V> m = elementData[index];
        while (m != null && (m.origKeyHash != keyHash || key != m.key)) {
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
        int hash = HashMapPrimitiveLongObject.longHash(key^hashSalt);
        int index = hash & (elementData.length - 1);
        entry = findNonNullKeyEntry(key, index, hash);
        if (entry == null) {
           modCount++;
           entry = createHashedEntry(key, index, hash);
           if (++elementCount > threshold) {
               rehash();
           }
        }

        V result = entry.value;
        entry.value = value;
        return result;
    }


    Entry<V> createHashedEntry(long key, int index, int hash) {
        Entry<V> entry = new Entry<V>(key,hash);
        entry.next = elementData[index];
        elementData[index] = entry;
        return entry;
    }
    
    void rehash(int capacity) {
        int length = calculateCapacity((capacity == 0 ? 1 : capacity << 1));

        Entry<V>[] newData = newElementArray(length);
        for (int i = 0; i < elementData.length; i++) {
            Entry<V> entry = elementData[i];
            elementData[i] = null;
            while (entry != null) {
                int index = entry.origKeyHash & (length - 1);
                Entry<V> next = entry.next;
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
    public V remove(long key) {
        Entry<V> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }


    final Entry<V> removeEntry(long key) {
        int index = 0;
        Entry<V> entry;
        Entry<V> last = null;

        int hash = HashMapPrimitiveLongObject.longHash(key^hashSalt);
        index = hash & (elementData.length - 1);
        entry = elementData[index];
        while (entry != null && !(entry.origKeyHash == hash && key == entry.key)) {
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
