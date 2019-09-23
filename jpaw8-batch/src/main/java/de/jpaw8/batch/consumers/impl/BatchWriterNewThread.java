package de.jpaw8.batch.consumers.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.batch.lmax.DataWithOrdinal;
import de.jpaw8.batch.lmax.TheEventFactory;

public class BatchWriterNewThread <E> extends BatchLinked implements BatchWriter<E> {
    private final BatchWriter<? super E> consumer;
    private final int bufferSize;
    private final int numThreads;

    private ExecutorService threads = null;
    private Disruptor<DataWithOrdinal<E>> disruptor = null;
    private RingBuffer<DataWithOrdinal<E>> rb = null;

    private final EventFactory<DataWithOrdinal<E>> factory = new TheEventFactory<E>();

    public BatchWriterNewThread(BatchWriter<? super E> consumer, int bufferSize) {
        super(consumer);
        this.consumer = consumer;
        this.bufferSize = bufferSize;
        numThreads = 1;
    }

    public BatchWriterNewThread(BatchWriter<? super E> consumer, int bufferSize, int numThreads) {
        super(consumer);
        this.consumer = consumer;
        this.bufferSize = bufferSize;
        this.numThreads = numThreads;
    }

    @Override
    public void open() throws Exception {
        // create an executorService
        threads = (numThreads <= 1) ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(numThreads) ;

        // Construct the Disruptor which interfaces the decoder to DB storage
        disruptor = new Disruptor<DataWithOrdinal<E>>(factory, bufferSize, threads);

        // Connect the handler - ST
        EventHandler<DataWithOrdinal<E>> handler = (data, sequence, isLast) -> consumer.store(data.data, data.recordno);
        disruptor.handleEventsWith(handler);

        // Connect the handler - MT - notice the difference between EventHandler (all get the same record) and WorkHandler (only one of all gets the recod)
//        WorkHandler<DataWithOrdinal<E>> [] handlerTab = new WorkHandler [numThreads];
//        for (int i = 0; i < numThreads; ++i)
//            handlerTab[i] = (data) -> whereToPut.accept(data.data, data.recordno);
//        disruptor.handleEventsWithWorkerPool(handlerTab);


        // Start the Disruptor, starts all threads running
        // and get the ring buffer from the Disruptor to be used for publishing.
        rb = disruptor.start();
    }

    @Override
    public void store(E record, int n) {
        long sequence = rb.next();  // Grab the next sequence
        try {
            DataWithOrdinal<E> event = rb.get(sequence);    // Get the entry in the Disruptor for the sequence
            event.data = record;                            // fill data
            event.recordno = n;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rb.publish(sequence);
        }
    }

    @Override
    public void close() throws Exception {
        // tell them it's done
        disruptor.shutdown();

        // shutdown the Executor
        threads.shutdown();

        // cleanup, allow GC to collect
        threads = null;
        rb = null;
        disruptor = null;
    }

}
