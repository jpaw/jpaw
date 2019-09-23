package de.jpaw8.batch.consumers.impl;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchWriter;

public class BatchWriterConsumerObjInt<E> implements BatchWriter<E> {
    private final ObjIntConsumer<? super E> consumer;

    public BatchWriterConsumerObjInt(ObjIntConsumer<? super E> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void store(E response, int no) {
        consumer.accept(response, no);
    }
}
