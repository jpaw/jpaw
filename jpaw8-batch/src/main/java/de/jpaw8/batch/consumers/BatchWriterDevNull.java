package de.jpaw8.batch.consumers;

import de.jpaw8.batch.api.BatchWriter;

public class BatchWriterDevNull<E> implements BatchWriter<E> {

    @Override
    public void store(E response, int i) {
    }
}
