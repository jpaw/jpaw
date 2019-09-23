package de.jpaw8.batch.consumers;

import de.jpaw8.batch.consumers.impl.BatchWriterTextFileAbstract;

public class BatchWriterTextFile extends BatchWriterTextFileAbstract<String> {

    public BatchWriterTextFile(String header, String footer) {
        super(header, footer);
    }
    public BatchWriterTextFile() {
        super();
    }

    @Override
    public void store(String response, int no) {
        super.write(response);
    }
}
