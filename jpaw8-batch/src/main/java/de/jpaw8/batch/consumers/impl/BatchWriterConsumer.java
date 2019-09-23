package de.jpaw8.batch.consumers.impl;

import java.util.function.Consumer;

import de.jpaw8.batch.api.BatchWriter;

public class BatchWriterConsumer<E> implements BatchWriter<E> {
    private final Consumer<? super E> consumer;

    public BatchWriterConsumer(Consumer<? super E> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void store(E response, int no) {
        consumer.accept(response);
    }
}
