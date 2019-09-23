package de.jpaw8.batch.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The central point of a multithreaded batch processing queue. From here, processing will be started.
 * Initialization of the input is done via the reader chain, initialization of the output via the writer chain (which may
 * be just a discard / /dev/null writer in the simplest case).
 *
 * It is assumed that command line parsing (if any is required) is performed first, for example via the CmdlineParserContext class.
 *
 * @param <E>
 */
public final class Batches<E> {
    private static final Logger LOG = LoggerFactory.getLogger(Batch.class);

    // some statistics data
    private final BatchReaderFactory<? extends E> reader;
    private final BatchWriterFactory<? super E> writer;

    public Batches(BatchReaderFactory<? extends E> reader, BatchWriterFactory<? super E> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public final void run() throws Exception {
//        CmdlineParserContext.getContext().parse(args);        // convenient for simple programs, but causes issues if multiple batches within an execution

        Date programStart = new Date();
        LOG.info("{}: Initializing processing pipeline", programStart);

        // first, enable the output queue.
        writer.open();

        // then, enable the input queue (this may already push initial messages through the queue).
        reader.open();

        Date parsingStart = new Date();
        LOG.info("{}: Starting to parse", parsingStart);

        // kick off the threads. This is done by the starting the readers main loop
        reader.produceTo(writer);      // this is the main processing loop

        Date parsingEnd = new Date();
        LOG.info("{}: Parsing the input complete", parsingEnd);

        // close the input queue to ensure no further messages are generated (this may still push some final messages through the queue).
        reader.close();

        // close the output queue (flushing any buffers)
        writer.close();

        Date programEnd = new Date();
        LOG.info("{}: Processing complete", programEnd);

        LOG.info("Parsing took {} ms, total time was {} ms",
                parsingEnd.getTime() - parsingStart.getTime(),
                programEnd.getTime() - programStart.getTime());
    }
    public final void runNoLog() throws Exception {
      writer.open();
      reader.open();
      reader.produceTo(writer);      // this is the main processing loop
      reader.close();
      writer.close();
  }

    // shorthand for new Batch<E>(reader, writer).run(args)
    public static <T> void run(BatchReaderFactory<? extends T> reader, BatchWriterFactory<? super T> writer) throws Exception {
        new Batches<T>(reader, writer).run();
    }
}
