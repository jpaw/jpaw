package de.jpaw.batch.filters;

import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.endpoints.BatchInputFilter;

public class HardcodedEvenOddFilter<B> extends BatchInputFilter<B> {
    private final boolean odd;

    public HardcodedEvenOddFilter(BatchReader<B> reader, boolean odd) {
        super(reader);
        this.odd = odd;
    }

    @Override
    public boolean test(B data) {
        // the superclasses "countTotal" is preincremented and counts 1..n
        if ((countTotal & 1) == 0)
            // second
            return !odd;
        else
            // first, third etc
            return odd;
    }

}
