package de.jpaw8.batch.consumers.impl;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;
import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.BatchWriterFactory;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchWriterFactoryMapForProcessorFactory<E,R> extends BatchLinked implements BatchWriterFactory<E> {
    private final BatchWriterFactory<? super R> consumer;
    private final BatchProcessorFactory<E,R> function;

    public BatchWriterFactoryMapForProcessorFactory(BatchWriterFactory<? super R> consumer, BatchProcessorFactory<E,R> function) {
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
    public BatchWriter<E> get(int threadno) {
        // construct a single instance of a mapper
        BatchWriter<? super R> downstreamConsumerInstance = consumer.get(threadno);
        BatchProcessor<E, R> functionInstance;
        try {
            functionInstance = function.getProcessor(threadno);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // link these two
        return new BatchWriterMapForProcessor<E,R>(downstreamConsumerInstance, functionInstance);
    }
}
