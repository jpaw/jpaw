package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

public class MilliUnits extends FixedPointBase<MilliUnits> {
    private static final long serialVersionUID = -466464673376366003L;
    public static final int DECIMALS = 3;
    public static final long UNIT_MANTISSA = 1000L;
    public static final double UNIT_SCALE = UNIT_MANTISSA;       // casted to double at class initialisation time
    public static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division
    public static final MilliUnits ZERO = new MilliUnits(0);
    public static final MilliUnits ONE = new MilliUnits(UNIT_MANTISSA);

    public MilliUnits(long mantissa) {
        super(mantissa);
    }

    public MilliUnits(double value) {
        super(Math.round(value * UNIT_SCALE));
    }

    public MilliUnits(String value) {
        super(parseMantissa(value, DECIMALS));
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static MilliUnits of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static MilliUnits valueOf(long value) {
        return ZERO.newInstanceOf(value * UNIT_MANTISSA);
    }

    /** Constructs an instance with a specified value specified via floating point. Take care for rounding issues! */
    public static MilliUnits valueOf(double value) {
        return ZERO.newInstanceOf(Math.round(value * UNIT_SCALE));
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static MilliUnits valueOf(String value) {
        return ZERO.newInstanceOf(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static MilliUnits of(FixedPointBase<?> that) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MilliUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static MilliUnits of(FixedPointBase<?> that, RoundingMode rounding) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MilliUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        // rescale
        return  MilliUnits.of(divide_longs(that.getMantissa(), powersOfTen[-scaleDiff], rounding));
    }

    public static MilliUnits of(BigDecimal number) {
        final int scaleOfBigDecimal = number.scale();
        if (scaleOfBigDecimal <= 0) {
            // the value of the BigDecimal is integral
            final long valueOfBigDecimal = number.longValue();
            return of(valueOfBigDecimal * powersOfTen[-scaleOfBigDecimal]);
        }
        // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
        // TODO: replace it by a zero GC version
        // blame JDK, there is not even a current method to determine if a BigDecimal is integral despite a scale > 0, nor to get its mantissa without creating additional objects
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).unscaledValue().longValue());
    }

    @Override
    public MilliUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == getMantissa())
            return this;
        return new MilliUnits(mantissa);
    }

    @Override
    public int scale() {
        return DECIMALS;
    }

    @Override
    public MilliUnits getZero() {
        return ZERO;
    }

    @Override
    public MilliUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

    @Override
    public MilliUnits getMyself() {
        return this;
    }

    // provide code for the bonaparte adapters, to avoid separate adapter classes
    public long marshal() {
        return getMantissa();
    }

    public static MilliUnits unmarshal(Long mantissa) {
        return mantissa == null ? null : ZERO.newInstanceOf(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
