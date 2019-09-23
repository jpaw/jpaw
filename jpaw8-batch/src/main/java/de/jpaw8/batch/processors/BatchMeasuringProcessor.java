package de.jpaw8.batch.processors;

import org.HdrHistogram.Histogram;

import de.jpaw8.batch.api.BatchProcessor;

public class BatchMeasuringProcessor<E,F> implements BatchProcessor<E,F> {
    private final BatchProcessor<E,F> processor;
    private final Histogram histogram = new Histogram(3600000000L, 3);

    public BatchMeasuringProcessor(BatchProcessor<E, F> processor) {
        super();
        this.processor = processor;
    }


    @Override
    public F process(E data, int recordNo) throws Exception {
        long start = System.nanoTime();
        F result = processor.process(data, recordNo);
        long end = System.nanoTime();
        histogram.recordValue(end - start);
        return result;
    }

    @Override
    public void close() throws Exception {
        System.out.println("Recorded latencies [ns]:");
        histogram.outputPercentileDistribution(System.out, 1.0);
    }
}
