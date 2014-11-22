package de.jpaw.batch.examples;

import de.jpaw.batch.endpoints.BatchReaderTextFile;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.impl.BatchExecutorMultiThreaded;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

public class BatchTextFileCopyMT {
    public static void main(String [] args) throws Exception {
        new BatchExecutorMultiThreaded<String,String>().run(args,
                new BatchReaderTextFile(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
