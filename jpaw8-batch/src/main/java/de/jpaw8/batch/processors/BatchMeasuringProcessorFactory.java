package de.jpaw8.batch.processors;

import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;

import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;

public class BatchMeasuringProcessorFactory<E,F> implements BatchProcessorFactory<E,F> {
    private final BatchProcessorFactory<E,F> delegate;
    private final Histogram histogram = new ConcurrentHistogram(3600000000L, 3);

    public BatchMeasuringProcessorFactory(BatchProcessorFactory<E, F> delegate) {
        super();
        this.delegate = delegate;
    }

    private static class LocalMeasuringProcessor<E1,F1> implements BatchProcessor<E1,F1> {
        private final BatchProcessor<E1,F1> processor;
        private final Histogram histogram;

        public LocalMeasuringProcessor(BatchProcessor<E1, F1> processor, Histogram histogram) {
            super();
            this.processor = processor;
            this.histogram = histogram;
        }

        @Override
        public F1 process(E1 data, int recordNo) throws Exception {
            long start = System.nanoTime();
            F1 result = processor.process(data, recordNo);
            long end = System.nanoTime();
            histogram.recordValue(end - start);
            return result;
        }

    }


    @Override
    public void close() throws Exception {
        System.out.println("Recorded latencies [ns]:");
        histogram.outputPercentileDistribution(System.out, 1.0);
    }


    @Override
    public BatchProcessor<E, F> getProcessor(int threadNo) throws Exception {
        // create a new instance of the delegate and link it with a new measuring processor
        return new LocalMeasuringProcessor<E, F>(delegate.getProcessor(threadNo), histogram);
    }
}
