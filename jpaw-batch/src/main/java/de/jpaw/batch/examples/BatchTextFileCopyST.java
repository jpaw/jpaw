package de.jpaw.batch.examples;

import de.jpaw.batch.endpoints.BatchReaderTextFile;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

public class BatchTextFileCopyST {
    public static void main(String [] args) throws Exception {
        new BatchExecutorUnthreaded<String,String>().run(args,
                new BatchReaderTextFile(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
