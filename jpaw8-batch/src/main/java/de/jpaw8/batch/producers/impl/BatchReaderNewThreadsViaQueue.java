package de.jpaw8.batch.producers.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.ObjIntConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.api.BatchReaderFactory;
import de.jpaw8.batch.api.BatchWriterFactory;
import de.jpaw8.batch.factories.ABQCollector;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.batch.lmax.DataWithOrdinal;

/** Allocates the processing of records into a number of parallel threads, using the LMAX disruptor. */
public class BatchReaderNewThreadsViaQueue<E> extends BatchLinked implements BatchReaderFactory<E> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchReaderNewThreadsViaQueue.class);
    private final BatchReader<? extends E> producer;
    private int bufferSize = 1024;
    private int numThreads = 1;
    private long timeout = 300L;
    private BlockingQueue<DataWithOrdinal<E>> inputQueue = null;

    private final CmdlineParserContext ctx;

    /** Threads determined by command line. */
    public BatchReaderNewThreadsViaQueue(BatchReader<? extends E> producer) {
        super(producer);
        this.producer = producer;
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("threads",   JSAP.INTEGER_PARSER,    "1", JSAP.NOT_REQUIRED, 't', "number of parallel threads");
        ctx.addFlaggedOption("queuesize", JSAP.INTEGER_PARSER, "1024", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "queue size");
        ctx.addFlaggedOption("timeout",   JSAP.INTEGER_PARSER,  "300", JSAP.NOT_REQUIRED, 'w', "maximum wait time per record, before a timeout occurs, in seconds, default 300 (5 minutes)");
    }

    /** hardcoded number of threads. */
    public BatchReaderNewThreadsViaQueue(BatchReader<? extends E> producer, int bufferSize, int numThreads) {
        super(producer);
        this.producer = producer;
        this.bufferSize = bufferSize;
        this.numThreads = numThreads;
        ctx = null;
    }


    private class ReaderCallback implements ObjIntConsumer<E> {
        @Override
        public void accept(E response, int no) {
            // inputQueue.put(record);
            try {
                boolean couldDoIt = inputQueue.offer(new DataWithOrdinal<E>(response, no), timeout, TimeUnit.SECONDS);
                if (!couldDoIt) {
                    LOG.error("Timeout occured trying to add record {} to the processing queue", no);
                    throw new RuntimeException("Couldn't store record " + no + " within " + timeout + " seconds");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
            if (bufferSize < 8) {
                LOG.error("Bad queue size. Must be at least 8. Using 8.");
                bufferSize = 8;
            }
            timeout = ctx.getInt("timeout");
        }

        // create an executorService
        ExecutorService threads = Executors.newFixedThreadPool(numThreads);

        inputQueue = new ArrayBlockingQueue<DataWithOrdinal<E>>(bufferSize);
        // set up the callables
        ABQCollector<E> [] handlerTab = new ABQCollector [numThreads];

        for (int i = 0; i < numThreads; ++i) {
            ABQCollector<E> task = new ABQCollector<E>(consumerFactory.get(i), inputQueue);
            handlerTab[i] = task;
            threads.submit(task);
        }

        // kick off the sender
        producer.produceTo(new ReaderCallback());

        // tell them it's done. every collector will collect one EOF object and then terminate itself
        DataWithOrdinal<E> eofObj = new DataWithOrdinal<E>(null, DataWithOrdinal.EOF);
        for (int i = 0; i < numThreads; ++i) {
            inputQueue.put(eofObj);
        }

        // shutdown the Executor
        threads.shutdown();

        // wait for tasks to have completed (essential because we want to cleanup the next pipeline steps)
        threads.awaitTermination(15, TimeUnit.MINUTES);
    }
}
