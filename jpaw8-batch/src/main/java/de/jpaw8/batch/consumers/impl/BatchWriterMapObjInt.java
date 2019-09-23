package de.jpaw8.batch.consumers.impl;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.function.ObjIntFunction;

public class BatchWriterMapObjInt<E,R> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super R> consumer;
    private final ObjIntFunction<E,R> function;

    public BatchWriterMapObjInt(BatchWriter<? super R> consumer, ObjIntFunction<E,R> function) {
        super(consumer);
        this.consumer = consumer;
        this.function = function;
    }

    @Override
    public void store(E response, int no) {
        consumer.store(function.apply(response, no), no);
    }
}
