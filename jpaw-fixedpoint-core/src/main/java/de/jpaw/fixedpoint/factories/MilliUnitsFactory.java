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
    public MilliUnits of(long mantissa) {
        return MilliUnits.of(mantissa);
    }

    @Override
    public MilliUnits valueOf(String s) {
        return MilliUnits.valueOf(s);
    }

    @Override
    public MilliUnits valueOf(long n) {
        return MilliUnits.valueOf(n);
    }

    @Override
    public MilliUnits valueOf(BigDecimal n) {
        return MilliUnits.of(n); // naming mismatch
    }

    @Override
    public MilliUnits valueOf(double n) {
        return MilliUnits.valueOf(n);
    }
}
