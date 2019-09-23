package de.jpaw8.batch.producers.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.ObjIntConsumer;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.batch.lmax.DataWithOrdinal;
import de.jpaw8.batch.lmax.TheConsumer;
import de.jpaw8.batch.lmax.TheEventFactory;

public class BatchReaderNewThread<E> extends BatchLinked implements BatchReader<E> {
    private final BatchReader<? extends E> producer;
    private final int bufferSize;

    private final EventFactory<DataWithOrdinal<E>> factory = new TheEventFactory<E>();

    public BatchReaderNewThread(BatchReader<? extends E> producer, int bufferSize) {
        super(producer);
        this.producer = producer;
        this.bufferSize = bufferSize;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super E> whereToPut) throws Exception {
        // create an executorService
        ExecutorService threads = Executors.newSingleThreadExecutor();

        // Construct the Disruptor which interfaces the decoder to DB storage
//        final Disruptor<DataWithOrdinal<E>> disruptor = new Disruptor<DataWithOrdinal<E>>(factory, bufferSize, threads);
        final Disruptor<DataWithOrdinal<E>> disruptor = new Disruptor<DataWithOrdinal<E>>(
                factory,
                bufferSize,
                threads
                // adding the next 2 lines should be faster by the Wiki, but seems to break it (exhaust all of the machine's resources)
//                , ProducerType.SINGLE, // Single producer
//                new BlockingWaitStrategy()
        );

        // Connect the handler - ST
        EventHandler<DataWithOrdinal<E>> handler = (data, sequence, isLast) -> whereToPut.accept(data.data, data.recordno);
        disruptor.handleEventsWith(handler);

        // Start the Disruptor, starts all threads running
        // and get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<DataWithOrdinal<E>> rb = disruptor.start();

        // kick off the sender
        producer.produceTo(new TheConsumer<E>(rb));

        // tell them it's done
        disruptor.shutdown();

        // shutdown the Executor
        threads.shutdown();
    }
}
