package de.jpaw8.batch.consumers;

import de.jpaw8.batch.consumers.impl.BatchWriterTextFileAbstract;

public class BatchWriterSimpleCSV extends BatchWriterTextFileAbstract<Boolean> {

    private String getResult(Boolean data) {
        if (data != null && data.booleanValue())
            return "OK";
        else
            return "ERROR";
    }

    @Override
    public void store(Boolean response, int no) {
        super.write(no + "," + getResult(response) + "\n");
    }
}
