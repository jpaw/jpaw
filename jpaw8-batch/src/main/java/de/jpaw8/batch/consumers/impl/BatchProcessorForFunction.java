package de.jpaw8.batch.consumers.impl;

import java.util.function.Function;

import de.jpaw8.batch.api.BatchProcessor;

public class BatchProcessorForFunction<E,R> implements BatchProcessor<E,R> {

    private final Function<? super E, ? extends R> function;

    public BatchProcessorForFunction(Function<? super E, ? extends R> function) {
        this.function = function;
    }

    @Override
    public R process(E data, int recordNo) throws Exception {
        return function.apply(data);
    }

    @Override
    public void close() throws Exception {
    }

}
