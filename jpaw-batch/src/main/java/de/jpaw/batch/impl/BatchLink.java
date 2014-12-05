package de.jpaw.batch.impl;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.batch.api.Contributor;

/** Allows to merge two contributing elements, is used for filters and transformers as a base class. */
public abstract class BatchLink implements Contributor {
    
    private Contributor theOther;
    
    protected BatchLink(Contributor theOther) {
        this.theOther = theOther;
    }
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        theOther.addCommandlineParameters(params);
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        theOther.evalCommandlineParameters(params);
    }

    @Override
    public void close() throws Exception {
        theOther.close();
    }

}
