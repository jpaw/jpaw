package de.jpaw8.batch.producers.impl;

import java.util.function.IntPredicate;
import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchReaderFilterInt<E> extends BatchLinked implements BatchReader<E> {
    private final BatchReader<? extends E> producer;
    private final IntPredicate ordinalFilter;

    public BatchReaderFilterInt(BatchReader<? extends E> producer, IntPredicate ordinalFilter) {
        super(producer);
        this.producer = producer;
        this.ordinalFilter = ordinalFilter;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super E> whereToPut) throws Exception {
        producer.produceTo((data, i) -> { if (ordinalFilter.test(i)) whereToPut.accept(data, i); });
    }
}
