package de.jpaw.batch.impl;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.batch.api.Contributor;

/** A stub suitable as a base class, with empty implementations. */
public class ContributorNoop implements Contributor {

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

}
