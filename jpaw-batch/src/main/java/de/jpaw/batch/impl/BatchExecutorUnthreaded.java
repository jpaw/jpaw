package de.jpaw.batch.impl;

import de.jpaw.batch.api.BatchProcessor;
import de.jpaw.batch.api.BatchProcessorFactory;
import de.jpaw.batch.api.BatchWriter;

public class BatchExecutorUnthreaded<E,F> extends BatchMain<E,F> {
    private BatchProcessor<E,F> localProcessor = null;
    private BatchWriter<? super F> localWriter = null;
    private int numRecords = 0;         // number of records read (added to input queue)
    private int numExceptions = 0;      // number of records which resulted in an exception

    @Override
    public void open(BatchProcessorFactory<E, F> processorFactory, BatchWriter<? super F> writer) throws Exception {
        localProcessor = processorFactory.getProcessor(0);
        localWriter = writer;
    }

    @Override
    public void close() throws Exception {
        localProcessor.close();
    }

    @Override
    public void accept(E record) {  // called by the reader
        ++numRecords;
        try {
            // process it immediately
            F result = localProcessor.process(numRecords, record);
            // and write the output to the writer
            localWriter.accept(numRecords, result);
        } catch (Exception e) {
            ++numExceptions;
        }
    }

    @Override
    public int getNumberOfRecordsTotal() {
        return numRecords;
    }
    @Override
    public int getNumberOfRecordsException() {
        return numExceptions;
    }
}
