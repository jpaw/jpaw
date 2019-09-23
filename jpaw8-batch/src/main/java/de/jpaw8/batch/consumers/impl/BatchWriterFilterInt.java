package de.jpaw8.batch.consumers.impl;

import java.util.function.IntPredicate;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchWriterFilterInt<E> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super E> consumer;
    private final IntPredicate filter;

    public BatchWriterFilterInt(BatchWriter<? super E> consumer, IntPredicate filter) {
        super(consumer);
        this.consumer = consumer;
        this.filter = filter;
    }

    @Override
    public void store(E response, int no) {
        if (filter.test(no))
            consumer.store(response, no);
    }
}
