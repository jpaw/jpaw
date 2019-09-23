package de.jpaw8.batch.processors;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;

/** The ECHO function: returns the parameter. */
public class BatchProcessorFactoryIdentity<X> implements BatchProcessorFactory<X,X> {

    @Override
    public BatchProcessor<X, X> getProcessor(int threadNo) {
        return new BatchProcessorIdentity<X>();
    }

    static private class BatchProcessorIdentity<X> implements BatchProcessor<X,X> {

        @Override
        public X process(X data, int recordNo) throws Exception {
            return data;
        }
    }
}
