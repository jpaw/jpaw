package de.jpaw8.batch.producers.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.api.BatchReaderFactory;
import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.BatchWriterFactory;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.batch.lmax.DataWithOrdinal;
import de.jpaw8.batch.lmax.TheConsumer;
import de.jpaw8.batch.lmax.TheEventFactory;

/** Allocates the processing of records into a number of parallel threads, using the LMAX disruptor. */
public class BatchReaderNewThreads<E> extends BatchLinked implements BatchReaderFactory<E> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchReaderNewThreads.class);
    private final BatchReader<? extends E> producer;
    private int bufferSize = 1024;
    private int numThreads = 1;

    private final EventFactory<DataWithOrdinal<E>> factory = new TheEventFactory<E>();
    private final CmdlineParserContext ctx;

    /** Threads determined by command line. */
    public BatchReaderNewThreads(BatchReader<? extends E> producer) {
        super(producer);
        this.producer = producer;
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("threads", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 't', "number of parallel threads");
        ctx.addFlaggedOption("queuesize", JSAP.INTEGER_PARSER, "1024", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "queue size (must be power of 2)");
    }

    /** hardcoded number of threads. */
    public BatchReaderNewThreads(BatchReader<? extends E> producer, int bufferSize, int numThreads) {
        super(producer);
        this.producer = producer;
        this.bufferSize = bufferSize;
        this.numThreads = numThreads;
        ctx = null;
    }

    private static class MyEventHandler<T> implements WorkHandler<DataWithOrdinal<? extends T>> {
        private final BatchWriter<T> consumer;

        private MyEventHandler(BatchWriter<T> consumer) {
            this.consumer = consumer;
        }
        @Override
        public void onEvent(DataWithOrdinal<? extends T> event) throws Exception {
            consumer.store(event.data, event.recordno);
        }

    }

    @Override
    public void produceTo(BatchWriterFactory<? super E> consumerFactory) throws Exception {
        if (ctx != null) {
            numThreads = ctx.getInt("threads");
            if (numThreads <= 0) {
                LOG.error("Bad number of threads. Must have at least 1 worker thread!");
                numThreads = 1;
            }
            bufferSize = ctx.getInt("queuesize");
            if (bufferSize < 8 || (bufferSize & (bufferSize - 1)) != 0) {
                LOG.error("Bad buffer size. Must be a power of 2 (at least 8). Using 1024.");
                bufferSize = 1024;
            }
        }

        // create an executorService
        ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        // Construct the Disruptor which interfaces the decoder to DB storage
        final Disruptor<DataWithOrdinal<E>> disruptor = new Disruptor<DataWithOrdinal<E>>(factory, bufferSize, threads);

        // Connect the handler - MT - notice the difference between EventHandler (all get the same record) and WorkHandler (only one of all gets the recod)
        WorkHandler<DataWithOrdinal<E>> [] handlerTab = new WorkHandler [numThreads];
        for (int i = 0; i < numThreads; ++i)
            handlerTab[i] = new MyEventHandler(consumerFactory.get(i));
        disruptor.handleEventsWithWorkerPool(handlerTab);


        // Start the Disruptor, starts all threads running
        // and get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<DataWithOrdinal<E>> rb = disruptor.start();

        // kick off the sender
        producer.produceTo(new TheConsumer<E>(rb));

        // tell them it's done
        disruptor.shutdown();

        // shutdown the Executor
        threads.shutdown();

        // wait for tasks to have completed (essential because we want to cleanup the next pipeline steps)
        threads.awaitTermination(15, TimeUnit.MINUTES);
    }
}
