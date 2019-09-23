package de.jpaw8.batch.consumers.impl;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.function.ObjIntPredicate;

public class BatchWriterFilterObjInt<E> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super E> consumer;
    private final ObjIntPredicate<? super E> filter;

    public BatchWriterFilterObjInt(BatchWriter<? super E> consumer, ObjIntPredicate<? super E> filter) {
        super(consumer);
        this.consumer = consumer;
        this.filter = filter;
    }

    @Override
    public void store(E response, int no) {
        if (filter.test(response, no))
            consumer.store(response, no);
    }
}
