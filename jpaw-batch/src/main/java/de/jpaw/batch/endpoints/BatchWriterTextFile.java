package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterTextFileAbstract;

public class BatchWriterTextFile extends BatchWriterTextFileAbstract implements BatchWriter<String> {

    @Override
    public void storeResult(int no, String response) throws Exception {
        super.write(response);
    }
}
