package de.jpaw.batch.xls2csv;


import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.poi.BatchReaderPoi;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;
import de.jpaw.batch.endpoints.BatchWriterTextFile;

public class Main {
    public static void main(String[] args) throws Exception {
        new BatchExecutorUnthreaded<String, String>().run(args,
                new BatchReaderPoi(),
                new BatchWriterTextFile(),
                new BatchProcessorFactoryIdentity<String>()
                );
    }
}
