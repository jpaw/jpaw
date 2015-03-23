package de.jpaw.batch.filters;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.endpoints.BatchInputFilter;

public class EvenOddFilter<B> extends BatchInputFilter<B> {
    private boolean odd = false;
    private boolean even = false;

    public EvenOddFilter(BatchReader<B> reader) {
        super(reader);
    }

    @Override
    public boolean test(B data) {
        // the superclasses "countTotal" is preincremented and counts 1..n
        if ((countTotal & 1) == 0)
            // second
            return even;
        else
            // first, third etc
            return odd;
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        super.addCommandlineParameters(params);
        params.registerParameter(new Switch("even", JSAP.NO_SHORTFLAG, "even", "use only even records"));
        params.registerParameter(new Switch("odd", JSAP.NO_SHORTFLAG, "odd", "use only odd records"));
    }
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        super.evalCommandlineParameters(params);
        even = params.getBoolean("even");
        odd = params.getBoolean("odd");
    }
}
