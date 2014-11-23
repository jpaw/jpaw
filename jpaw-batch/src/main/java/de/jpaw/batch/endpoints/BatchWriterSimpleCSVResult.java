package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterTextFileAbstract;

public class BatchWriterSimpleCSVResult extends BatchWriterTextFileAbstract implements BatchWriter<Boolean> {

    private String getResult(Boolean data) {
        if (data != null && data.booleanValue())
            return "OK";
        else
            return "ERROR";
    }
    
    @Override
    public void apply(int no, Boolean response) throws Exception {
        super.write(no + "," + getResult(response) + "\n");
    }
}
