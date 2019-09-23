package de.jpaw8.batch.producers.impl;

import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchReaderFilter<E> extends BatchLinked implements BatchReader<E> {
    private final BatchReader<? extends E> producer;
    private final Predicate<? super E> filter;

    public BatchReaderFilter(BatchReader<? extends E> producer, Predicate<? super E> filter) {
        super(producer);
        this.producer = producer;
        this.filter = filter;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super E> whereToPut) throws Exception {
        producer.produceTo((data, i) -> { if (filter.test(data)) whereToPut.accept(data, i); });
    }
}
