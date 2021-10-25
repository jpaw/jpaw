package de.jpaw.fixedpoint.factories;

import java.math.BigDecimal;

import de.jpaw.fixedpoint.FixedPointFactory;
import de.jpaw.fixedpoint.types.MicroUnits;

public class MicroUnitsFactory implements FixedPointFactory<MicroUnits> {

    @Override
    public Class<MicroUnits> instanceClass() {
        return MicroUnits.class;
    }

    @Override
    public MicroUnits getZero() {
        return MicroUnits.ZERO;
    }

    @Override
    public MicroUnits getOne() {
        return MicroUnits.ONE;
    }

    @Override
    public MicroUnits of(final long mantissa) {
        return MicroUnits.of(mantissa);
    }

    @Override
    public MicroUnits valueOf(final String s) {
        return MicroUnits.valueOf(s);
    }

    @Override
    public MicroUnits valueOf(final long n) {
        return MicroUnits.valueOf(n);
    }

    @Override
    public MicroUnits valueOf(final BigDecimal n) {
        return MicroUnits.of(n); // naming mismatch
    }

    @Override
    public MicroUnits valueOf(final double n) {
        return MicroUnits.valueOf(n);
    }
}
