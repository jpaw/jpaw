package de.jpaw8.batch.factories;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.BatchWriterFactory;
import de.jpaw8.batch.lmax.DataWithOrdinal;

/** Collects data from multiple writer threads and joins it into a single one, using the BlockingQueue.
 * The writer is moved into a separate thread. */
public class Collector<E> extends BatchLinked implements BatchWriterFactory<E> {
    public static int EOF = -1;

    private final BlockingQueue<DataWithOrdinal<E>> outputQueue;
    private final Thread collector;

    public Collector(BatchWriter<? super E> consumer) {
        super(consumer);
        int outQueueSize = 1024;
        this.outputQueue = new ArrayBlockingQueue<DataWithOrdinal<E>>(outQueueSize);
        collector = new Thread(new ABQCollector<E>(consumer, outputQueue));
    }


    private class QueueStorer implements BatchWriter<E> {
        @Override
        public void store(E response, int no) {
            try {
                outputQueue.put(new DataWithOrdinal<E>(response, no));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void open() throws Exception {
        super.open();
        // start the collector
        collector.start();
    }

    // get a writer for a specific thread
    @Override
    public BatchWriter<E> get(int threadno) {
        return new QueueStorer();
    }

    @Override
    public void close() throws Exception {

        // then push the EOF for the collector to the output queue...
        outputQueue.put(new DataWithOrdinal<E>(null, EOF));

        // and wait for writer to finish...
        collector.join();

        // close the underlying writer
        super.close();
    }

}
