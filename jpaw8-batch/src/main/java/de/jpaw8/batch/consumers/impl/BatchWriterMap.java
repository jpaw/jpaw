package de.jpaw8.batch.consumers.impl;

import java.util.function.Function;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchWriterMap<E,R> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super R> consumer;
    private final Function<? super E, ? extends R> function;

    public BatchWriterMap(BatchWriter<? super R> consumer, Function<? super E, ? extends R> function) {
        super(consumer);
        this.consumer = consumer;
        this.function = function;
    }

    @Override
    public void store(E response, int no) {
        consumer.store(function.apply(response), no);
    }
}
