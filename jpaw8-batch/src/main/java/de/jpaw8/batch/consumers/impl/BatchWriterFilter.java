package de.jpaw8.batch.consumers.impl;

import java.util.function.Predicate;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchWriterFilter<E> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super E> consumer;
    private final Predicate<? super E> filter;

    public BatchWriterFilter(BatchWriter<? super E> consumer, Predicate<? super E> filter) {
        super(consumer);
        this.consumer = consumer;
        this.filter = filter;
    }

    @Override
    public void store(E response, int no) {
        if (filter.test(response))
            consumer.store(response, no);
    }
}
