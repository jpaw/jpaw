package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.impl.ContributorNoop;

/** Batch reader for testing. This one represents an empty source. */
public class BatchReaderEmpty<E> extends ContributorNoop implements BatchReader<E> {
    @Override
    public void produceTo(BatchMainCallback<? super E> whereToPut) {
    }
}
