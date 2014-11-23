package de.jpaw.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.ContributorNoop;

public class BatchWriterDevNull<E> extends ContributorNoop implements BatchWriter<E> {

    @Override
    public void apply(int no, E response) {
    }
}
