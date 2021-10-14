package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

public class FemtoUnits extends FixedPointBase<FemtoUnits> {
    private static final long serialVersionUID = -4664646733763660015L;
    public static final int DECIMALS = 15;
    public static final long UNIT_MANTISSA = 1_000_000_000_000_000L;
    public static final double UNIT_SCALE = UNIT_MANTISSA;       // cast to double at class initialization time
    public static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division
    public static final FemtoUnits ZERO = new FemtoUnits(0);
    public static final FemtoUnits ONE = new FemtoUnits(UNIT_MANTISSA);

    // external callers use valueOf factory method, which returns existing objects for 0 and 1. This constructor is used by the factory methods
    private FemtoUnits(long mantissa) {
        super(mantissa);
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static FemtoUnits of(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        return new FemtoUnits(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static FemtoUnits valueOf(long value) {
        return of(value * UNIT_MANTISSA);
    }

    /** Constructs an instance with a specified value specified via floating point. Take care for rounding issues! */
    public static FemtoUnits valueOf(double value) {
        return of(Math.round(value * UNIT_SCALE));
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static FemtoUnits valueOf(String value) {
        return of(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static FemtoUnits of(FixedPointBase<?> that) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return FemtoUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static FemtoUnits of(FixedPointBase<?> that, RoundingMode rounding) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return FemtoUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        // rescale
        return  FemtoUnits.of(divide_longs(that.getMantissa(), powersOfTen[-scaleDiff], rounding));
    }

    public static FemtoUnits of(BigDecimal number) {
        final int scaleOfBigDecimal = number.scale();
        if (scaleOfBigDecimal <= 0) {
            // the value of the BigDecimal is integral
            return of(number.longValue() * UNIT_MANTISSA);
        }
        // This is certainly not the most efficient implementation, as it involves the construction of up to one new BigDecimal and a BigInteger
        // TODO: replace it by a zero GC version
        // blame JDK, there is not even a current method to determine if a BigDecimal is integral despite a scale > 0, nor to get its mantissa without creating additional objects
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).unscaledValue().longValue());
    }

    @Override
    public FemtoUnits newInstanceOf(long mantissa) {
        if (mantissa == this.mantissa)
            return this;
        return of(mantissa);
    }

    @Override
    public int scale() {
        return DECIMALS;
    }

    @Override
    public FemtoUnits getZero() {
        return ZERO;
    }

    @Override
    public FemtoUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

    @Override
    public FemtoUnits getMyself() {
        return this;
    }

    // provide code for the bonaparte adapters, to avoid separate adapter classes
    public long marshal() {
        return mantissa;
    }

    public static FemtoUnits unmarshal(Long mantissa) {
        return mantissa == null ? null : of(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
