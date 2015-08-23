package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchFileReader;
import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.impl.BatchReaderTextFileAbstract;

public class BatchReaderTextFile extends BatchReaderTextFileAbstract implements BatchFileReader<String> {

    @Override
    public void produceTo(BatchMainCallback<? super String> whereToPut) throws Exception {
        String line;
        while ((line = getNext()) != null) {
            whereToPut.accept(line);
        }
    }
}
