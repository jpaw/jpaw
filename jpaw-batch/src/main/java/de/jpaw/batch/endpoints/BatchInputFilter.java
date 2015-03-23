package de.jpaw.batch.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.batch.api.BatchFilter;
import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.impl.BatchLink;

public abstract class BatchInputFilter<B> extends BatchLink implements BatchFilter<B>, BatchReader<B>, BatchMainCallback<B> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchInputFilter.class);
    protected BatchReader<B> primaryReader;
    private BatchMainCallback<? super B> myTarget;
    protected int countTotal = 0;
    protected int countUsed = 0;

    public BatchInputFilter(BatchReader<B> primaryReader) {
        super(primaryReader);
        this.primaryReader = primaryReader;
    }

    @Override
    public final void accept(B record) {
        ++countTotal;
        if (test(record)) {
            ++countUsed;
            myTarget.accept(record);
        }
    }

    @Override
    public void produceTo(BatchMainCallback<? super B> whereToPut) throws Exception {
        myTarget = whereToPut;
        primaryReader.produceTo(this);
        LOG.info("Input filter {} used {} records of a total {}", this.getClass().getSimpleName(), countUsed, countTotal);
    }

}
