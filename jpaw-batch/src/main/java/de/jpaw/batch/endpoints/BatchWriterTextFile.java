package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterTextFileAbstract;

public class BatchWriterTextFile extends BatchWriterTextFileAbstract implements BatchWriter<String> {

    public BatchWriterTextFile(String header, String footer) {
        super(header, footer);
    }
    public BatchWriterTextFile() {
        super();
    }

    @Override
    public void apply(int no, String response) throws Exception {
        super.write(response);
    }
}
