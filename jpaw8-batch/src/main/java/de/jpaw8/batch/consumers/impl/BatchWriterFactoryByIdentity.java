package de.jpaw8.batch.consumers.impl;

import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.BatchWriterFactory;

/** A class to return just the same reference of some provided writer (immutable consumers). */
public class BatchWriterFactoryByIdentity<E> implements BatchWriterFactory<E> {
    private final BatchWriter<E> writer;

    public BatchWriterFactoryByIdentity(BatchWriter<E> writer) {
        this.writer = writer;
    }

    public BatchWriterFactoryByIdentity(ObjIntConsumer<E> consumer) {
        this.writer = new BatchWriterConsumerObjInt<E>(consumer);
    }

    public BatchWriterFactoryByIdentity(Consumer<E> consumer) {
        this.writer = new BatchWriterConsumer<E>(consumer);
    }

    @Override
    public BatchWriter<E> get(int threadno) {
        return writer;
    }

}
