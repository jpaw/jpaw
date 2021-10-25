package de.jpaw.fixedpoint.factories;

import java.math.BigDecimal;

import de.jpaw.fixedpoint.FixedPointFactory;
import de.jpaw.fixedpoint.types.MilliUnits;

public class MilliUnitsFactory implements FixedPointFactory<MilliUnits> {

    @Override
    public Class<MilliUnits> instanceClass() {
        return MilliUnits.class;
    }

    @Override
    public MilliUnits getZero() {
        return MilliUnits.ZERO;
    }

    @Override
    public MilliUnits getOne() {
        return MilliUnits.ONE;
    }

    @Override
    public MilliUnits of(final long mantissa) {
        return MilliUnits.of(mantissa);
    }

    @Override
    public MilliUnits valueOf(final String s) {
        return MilliUnits.valueOf(s);
    }

    @Override
    public MilliUnits valueOf(final long n) {
        return MilliUnits.valueOf(n);
    }

    @Override
    public MilliUnits valueOf(final BigDecimal n) {
        return MilliUnits.of(n); // naming mismatch
    }

    @Override
    public MilliUnits valueOf(final double n) {
        return MilliUnits.valueOf(n);
    }
}
