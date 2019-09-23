package de.jpaw8.batch.consumers.impl;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchWriterMapForProcessor<E,R> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super R> consumer;
    private final BatchProcessor<E,R> function;

    public BatchWriterMapForProcessor(BatchWriter<? super R> consumer, BatchProcessor<E,R> function) {
        super(consumer);
        this.consumer = consumer;
        this.function = function;
    }

    @Override
    public void close() throws Exception {
        // first, close the function
        function.close();
        // then, any linked / chained writer
        super.close();
    }

    @Override
    public void store(E response, int no) {
        try {
            consumer.store(function.process(response, no), no);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
