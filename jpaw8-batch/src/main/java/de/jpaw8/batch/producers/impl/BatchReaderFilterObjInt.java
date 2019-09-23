package de.jpaw8.batch.producers.impl;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.function.ObjIntPredicate;

public class BatchReaderFilterObjInt<E> extends BatchLinked implements BatchReader<E> {
    private final BatchReader<? extends E> producer;
    private final ObjIntPredicate<? super E> biFilter;

    public BatchReaderFilterObjInt(BatchReader<? extends E> producer, ObjIntPredicate<? super E> biFilter) {
        super(producer);
        this.producer = producer;
        this.biFilter = biFilter;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super E> whereToPut) throws Exception {
        producer.produceTo((data, i) -> { if (biFilter.test(data, i)) whereToPut.accept(data, i); });
    }
}
