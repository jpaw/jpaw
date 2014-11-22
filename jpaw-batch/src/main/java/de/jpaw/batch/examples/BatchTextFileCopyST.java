package de.jpaw.batch.examples;

import de.jpaw.batch.endpoints.BatchReaderTextFile;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.impl.BatchMain;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;

public class BatchTextFileCopyST {
    public static void main(String [] args) throws Exception {
        new BatchMain<String,String>().runST(args,
                new BatchReaderTextFile(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
