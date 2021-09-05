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
    public NanoUnits of(long mantissa) {
        return NanoUnits.of(mantissa);
    }

    @Override
    public NanoUnits valueOf(String s) {
        return NanoUnits.valueOf(s);
    }

    @Override
    public NanoUnits valueOf(long n) {
        return NanoUnits.valueOf(n);
    }

    @Override
    public NanoUnits valueOf(BigDecimal n) {
        return NanoUnits.of(n); // naming mismatch
    }

    @Override
    public NanoUnits valueOf(double n) {
        return NanoUnits.valueOf(n);
    }
}
