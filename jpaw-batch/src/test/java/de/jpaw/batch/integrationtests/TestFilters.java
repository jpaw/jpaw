package de.jpaw.batch.integrationtests;

import org.testng.annotations.Test;

import de.jpaw.batch.endpoints.BatchReaderRepeater;
import de.jpaw.batch.endpoints.BatchWriterDevNull;
import de.jpaw.batch.filters.EvenOddFilter;
import de.jpaw.batch.filters.HardcodedEvenOddFilter;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

public class TestFilters {
    static private final String TESTDATA = "Hello, world";
    
    @Test
    public void testHardCodedEvenOddFilter() throws Exception {
        String [] cmdline = { "-n", "3" };
        
        new BatchExecutorUnthreaded<String,String>().run(
                cmdline,
                new HardcodedEvenOddFilter<String>(new BatchReaderRepeater<String>(TESTDATA), true),
                new BatchWriterDevNull<String>(),
                new BatchProcessorFactoryIdentity<String>());
    }

    @Test
    public void testCmdlineEvenOddFilter() throws Exception {
        String [] cmdline = { "-n", "3" };
        
        new BatchExecutorUnthreaded<String,String>().run(
                cmdline,
                new EvenOddFilter<String>(new BatchReaderRepeater<String>(TESTDATA)),
                new BatchWriterDevNull<String>(),
                new BatchProcessorFactoryIdentity<String>());
    }
}
