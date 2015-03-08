package de.jpaw.batch.impl;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.api.DataWithOrdinal;

/** reads the outpt queue and stores all records in the selected writer.
 * A single thread of this is running. */
public class BatchExecutorMTResultCollector<F> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(BatchExecutorMTResultCollector.class);

    private final BlockingQueue<DataWithOrdinal<F>> outputQueue;
    private final BatchWriter<? super F> writer;
    // redundant counters...
    private int numProcessed = 0;
    private int numExceptions = 0;

    public BatchExecutorMTResultCollector(BlockingQueue<DataWithOrdinal<F>> outputQueue, BatchWriter<? super F> writer) {
        this.outputQueue = outputQueue;
        this.writer = writer;
    }

    @Override
    public void run() {

        while (true) {
            DataWithOrdinal<F> newRecord = null;
            try {
                newRecord = outputQueue.take();
            } catch (InterruptedException e) {
                // interrupt means end of processing, we are done!
                break;
            }
            if (newRecord.recordno == BatchExecutorMultiThreaded.EOF)  // record number -1 means EOF
                break;
            // we got a record
            ++numProcessed;
            try {
                writer.accept(newRecord.recordno, newRecord.data);
            } catch (Exception e) {
                ++numExceptions;
            }
        }
        LOG.info("Result collector processed {} records ({} error)", numProcessed, numExceptions);
    }

}
