package de.jpaw8.batch.producers;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;

/** Batch reader for testing. This one represents an empty source. */
public class BatchReaderEmpty<E> implements BatchReader<E> {
    @Override
    public void produceTo(ObjIntConsumer<? super E> whereToPut) {
    }
}
