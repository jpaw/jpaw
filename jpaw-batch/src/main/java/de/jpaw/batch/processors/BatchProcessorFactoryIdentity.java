package de.jpaw.batch.processors;

import de.jpaw.batch.api.BatchProcessor;
import de.jpaw.batch.api.BatchProcessorFactory;
import de.jpaw.batch.impl.ContributorNoop;

/** The ECHO function: returns the parameter. */
public class BatchProcessorFactoryIdentity<X> extends ContributorNoop implements BatchProcessorFactory<X, X> {

    @Override
    public BatchProcessor<X, X> getProcessor(int threadNo) {
        return new BatchProcessorIdentity<X>();
    }

    private static class BatchProcessorIdentity<X> implements BatchProcessor<X, X> {

        @Override
        public X process(int recordNo, X data) throws Exception {
            return data;
        }

        @Override
        public void close() throws Exception {
        }
    }
}
