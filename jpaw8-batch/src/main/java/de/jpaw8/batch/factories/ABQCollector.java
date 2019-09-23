package de.jpaw8.batch.factories;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.lmax.DataWithOrdinal;

// ABQCollector implements a reader thread which takes items from the queue and sends them to the dedicated assigned consumer.

/** ArrayBlockingQueue collector. */
public class ABQCollector<E> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ABQCollector.class);

    private final BatchWriter<? super E> consumer;
    private final BlockingQueue<DataWithOrdinal<E>> queue;

    public ABQCollector(BatchWriter<? super E> consumer, BlockingQueue<DataWithOrdinal<E>> queue) {
        this.consumer = consumer;
        this.queue = queue;
    }


    @Override
    public void run() {
        int numProcessed = 0;
        int numExceptions = 0;

        while (true) {
            DataWithOrdinal<E> newRecord = null;
            try {
                newRecord = queue.take();
            } catch (InterruptedException e) {
                // interrupt means end of processing, we are done!
                break;
            }
            if (newRecord.recordno == DataWithOrdinal.EOF)  // record number -1 means EOF
                break;
            // we got a record
            ++numProcessed;
            try {
                consumer.store(newRecord.data, newRecord.recordno);
            } catch (Exception e) {
                ++numExceptions;
            }
        }
        LOG.info("Result collector processed {} records ({} error)", numProcessed, numExceptions);
    }
}
