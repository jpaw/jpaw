package de.jpaw8.batch.lmax;

/** Represents a generic data record with an integral ordinal attached to it.
 *
 *  Shared by all LMAX related dispatchers and consumers.
 *
 *   The purpose of the object is to represent the data while it sits within the ringbuffers.
 *   It is a mutable object on purpose, because it is preallocated and reinitialized during
 *   operation of the disruptor. The object is not intended to be passed to non-LMAX disruptor
 *   related classes.
 *   */
public class DataWithOrdinal<E> {
    public static final int EOF = -1;
//    public static <T> DataWithOrdinal<T> EOF_OBJ = new DataWithOrdinal(null, EOF);

    public E data;                                      // the actual payload
    public int recordno;                                // just a counter 1...n
    public volatile long p1, p2, p3, p4, p5, p6 = 7L;   // avoid false sharing

    public DataWithOrdinal(E data, int n) {
        this.data = data;
        this.recordno = n;
    }
}
