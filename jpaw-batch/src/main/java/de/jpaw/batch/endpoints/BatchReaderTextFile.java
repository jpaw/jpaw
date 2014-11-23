package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.impl.BatchReaderTextFileAbstract;

public class BatchReaderTextFile extends BatchReaderTextFileAbstract implements BatchReader<String> {

    @Override
    public void produceTo(BatchMainCallback<? super String> whereToPut) throws Exception {
        String line;
        while ((line = getNext()) != null) {
            whereToPut.accept(line);
        }
    }
}
