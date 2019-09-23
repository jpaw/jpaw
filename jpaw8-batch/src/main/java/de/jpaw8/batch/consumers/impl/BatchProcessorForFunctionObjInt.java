package de.jpaw8.batch.consumers.impl;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.function.ObjIntFunction;

public class BatchProcessorForFunctionObjInt<E,R> implements BatchProcessor<E,R> {

    private final ObjIntFunction<? super E, ? extends R> function;

    public BatchProcessorForFunctionObjInt(ObjIntFunction<? super E, ? extends R> function) {
        this.function = function;
    }

    @Override
    public R process(E data, int recordNo) throws Exception {
        return function.apply(data, recordNo);
    }

    @Override
    public void close() throws Exception {
    }

}
