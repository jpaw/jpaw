package de.jpaw.fixedpoint.factories;

import java.math.BigDecimal;

import de.jpaw.fixedpoint.FixedPointFactory;
import de.jpaw.fixedpoint.types.NanoUnits;

public class NanoUnitsFactory implements FixedPointFactory<NanoUnits> {

    @Override
    public Class<NanoUnits> instanceClass() {
        return NanoUnits.class;
    }

    @Override
    public NanoUnits getZero() {
        return NanoUnits.ZERO;
    }

    @Override
    public NanoUnits getOne() {
        return NanoUnits.ONE;
    }

    @Override
    public NanoUnits of(final long mantissa) {
        return NanoUnits.of(mantissa);
    }

    @Override
    public NanoUnits valueOf(final String s) {
        return NanoUnits.valueOf(s);
    }

    @Override
    public NanoUnits valueOf(final long n) {
        return NanoUnits.valueOf(n);
    }

    @Override
    public NanoUnits valueOf(final BigDecimal n) {
        return NanoUnits.of(n); // naming mismatch
    }

    @Override
    public NanoUnits valueOf(final double n) {
        return NanoUnits.valueOf(n);
    }
}
