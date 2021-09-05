package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

public class Tenths extends FixedPointBase<Tenths> {
    private static final long serialVersionUID = -466464673376366001L;
    public static final int DECIMALS = 1;
    public static final long UNIT_MANTISSA = 10L;
    public static final double UNIT_SCALE = UNIT_MANTISSA;       // casted to double at class initialisation time
    public static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division
    public static final Tenths ZERO = new Tenths(0);
    public static final Tenths ONE = new Tenths(UNIT_MANTISSA);

    public Tenths(long mantissa) {
        super(mantissa);
    }

    public Tenths(double value) {
        super(Math.round(value * UNIT_SCALE));
    }

    public Tenths(String value) {
        super(parseMantissa(value, DECIMALS));
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static Tenths of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static Tenths valueOf(long value) {
        return ZERO.newInstanceOf(value * UNIT_MANTISSA);
    }

    /** Constructs an instance with a specified value specified via floating point. Take care for rounding issues! */
    public static Tenths valueOf(double value) {
        return ZERO.newInstanceOf(Math.round(value * UNIT_SCALE));
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static Tenths valueOf(String value) {
        return ZERO.newInstanceOf(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static Tenths of(FixedPointBase<?> that) {
        int scaleDiff = DECIMALS - that.getScale();
        if (scaleDiff >= 0)
            return Tenths.of(that.getMantissa() * powersOfTen[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static Tenths of(FixedPointBase<?> that, RoundingMode rounding) {
        int scaleDiff = DECIMALS - that.getScale();
        if (scaleDiff >= 0)
            return Tenths.of(that.getMantissa() * powersOfTen[scaleDiff]);
        // rescale
        return  Tenths.of(divide_longs(that.getMantissa(), powersOfTen[-scaleDiff], rounding));
    }

    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static Tenths of(BigDecimal number) {
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).scaleByPowerOfTen(DECIMALS).longValue());
    }

    @Override
    public Tenths newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == getMantissa())
            return this;
        return new Tenths(mantissa);
    }

    @Override
    public int getScale() {
        return DECIMALS;
    }

    @Override
    public Tenths getZero() {
        return ZERO;
    }

    @Override
    public Tenths getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

    @Override
    public Tenths getMyself() {
        return this;
    }

    // provide code for the bonaparte adapters, to avoid separate adapter classes
    public long marshal() {
        return getMantissa();
    }

    public static Tenths unmarshal(Long mantissa) {
        return mantissa == null ? null : ZERO.newInstanceOf(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
