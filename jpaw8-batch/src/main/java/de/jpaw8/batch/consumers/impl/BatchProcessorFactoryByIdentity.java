package de.jpaw8.batch.consumers.impl;

import java.util.function.Function;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;
import de.jpaw8.function.ObjIntFunction;

/** A class to return just the same reference of some provided writer (immutable consumers). */
public class BatchProcessorFactoryByIdentity<E,R> implements BatchProcessorFactory<E,R> {
    private final BatchProcessor<E, R> processor;

    public BatchProcessorFactoryByIdentity(BatchProcessor<E, R> processor) {
        this.processor = processor;
    }

    public BatchProcessorFactoryByIdentity(ObjIntFunction<? super E, ? extends R> function) {
        this.processor = new BatchProcessorForFunctionObjInt<E,R>(function);
    }

    public BatchProcessorFactoryByIdentity(Function<? super E, ? extends R> function) {
        this.processor = new BatchProcessorForFunction<E,R>(function);
    }

    @Override
    public BatchProcessor<E, R> getProcessor(int threadNo) throws Exception {
        return processor;
    }

}
