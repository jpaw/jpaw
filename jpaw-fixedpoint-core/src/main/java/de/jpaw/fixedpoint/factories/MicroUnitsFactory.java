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
    public MicroUnits of(long mantissa) {
        return MicroUnits.of(mantissa);
    }

    @Override
    public MicroUnits valueOf(String s) {
        return MicroUnits.valueOf(s);
    }

    @Override
    public MicroUnits valueOf(long n) {
        return MicroUnits.valueOf(n);
    }

    @Override
    public MicroUnits valueOf(BigDecimal n) {
        return MicroUnits.of(n); // naming mismatch
    }

    @Override
    public MicroUnits valueOf(double n) {
        return MicroUnits.valueOf(n);
    }
}
