package de.jpaw.batch.integrationtests;

import org.junit.Test;

import de.jpaw.batch.endpoints.BatchReaderRepeater;
import de.jpaw.batch.endpoints.BatchWriterDevNull;
import de.jpaw.batch.impl.BatchExecutor3Threads;
import de.jpaw.batch.impl.BatchExecutorMultiThreaded;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.impl.BatchMain;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

// disclaimer: the benchmarks as shown here don't make a lot of sense, as the processing body is empty
public class Benchmarks {
    private void runMain(BatchMain<String, String> engine, String... cmdline) throws Exception {
        engine.run(cmdline, new BatchReaderRepeater<String>("Hello, world"), new BatchWriterDevNull<String>(), new BatchProcessorFactoryIdentity<String>());
    }

    @Test
    public void testTmpST() throws Exception {
        runMain(new BatchExecutorUnthreaded<String, String>(), "-n", "10000");
    }

    @Test
    public void testTmpMT4() throws Exception {
        runMain(new BatchExecutorMultiThreaded<String, String>(), "-n", "10000", "-t", "4");
    }

    @Test
    public void testTmpLMAX() throws Exception {
        runMain(new BatchExecutor3Threads<String, String>(), "-n", "10000");
    }

}
