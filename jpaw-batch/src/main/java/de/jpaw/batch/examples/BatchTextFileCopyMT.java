package de.jpaw.batch.examples;

import de.jpaw.batch.endpoints.BatchReaderTextFile;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.impl.BatchMain;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

public class BatchTextFileCopyMT {
    public static void main(String [] args) throws Exception {
        new BatchMain<String,String>().runMT(args,
                new BatchReaderTextFile(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
