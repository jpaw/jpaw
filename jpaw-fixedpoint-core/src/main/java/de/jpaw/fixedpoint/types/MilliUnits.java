package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

/** Instances of this class represent numbers with a fixed precision of 3 decimals, and up to 18 total digits precision. */
public final class MilliUnits extends FixedPointBase<MilliUnits> {
    private static final long serialVersionUID = -466464673376366003L;
    private static final int DECIMALS = 3;
    private static final long UNIT_MANTISSA = 1_000L;
    private static final double UNIT_SCALE = UNIT_MANTISSA;       // cast to double at class initialization time
    private static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division

    /** The representation of the number 0 in this class. This implementation attempts to maintain a single instance of 0 only. */
    public static final MilliUnits ZERO = new MilliUnits(0);

    /** The representation of the number 1 in this class. This implementation attempts to maintain a single instance of 0 only. */
    public static final MilliUnits ONE = new MilliUnits(UNIT_MANTISSA);

    // external callers use valueOf factory method, which returns existing objects for 0 and 1. This constructor is used by the factory methods
    private MilliUnits(final long mantissa) {
        super(mantissa);
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static MilliUnits of(final long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        return new MilliUnits(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static MilliUnits valueOf(final long value) {
        return of(value * UNIT_MANTISSA);
    }

    /** Constructs an instance with a value specified via a parameter of type double. */
    public static MilliUnits valueOf(final double value) {
        return of(Math.round(value * UNIT_SCALE));
    }

    /** Constructs an instance with a value specified via string representation. */
    public static MilliUnits valueOf(final String value) {
        return of(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of another fixed point type. Loosing precision is not supported. */
    public static MilliUnits of(final FixedPointBase<?> that) {
        final int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MilliUnits.of(that.getMantissa() * POWERS_OF_TEN[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static MilliUnits of(final FixedPointBase<?> that, final RoundingMode rounding) {
        final int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MilliUnits.of(that.getMantissa() * POWERS_OF_TEN[scaleDiff]);
        // rescale
        return  MilliUnits.of(divide_longs(that.getMantissa(), POWERS_OF_TEN[-scaleDiff], rounding));
    }

    /** Constructs an instance with a value specified via a parameter of type <code>BigDecimal</code>.
     * Deprecated. Use valueOf() instead. */
    @Deprecated
    public static MilliUnits of(final BigDecimal number) {
        return valueOf(number);
    }

    /** Constructs an instance with a value specified via a parameter of type <code>BigDecimal</code>. */
    public static MilliUnits valueOf(final BigDecimal number) {
        final int scaleOfBigDecimal = number.scale();
        if (scaleOfBigDecimal <= 0) {
            // the value of the BigDecimal is integral
            return of(number.longValue() * UNIT_MANTISSA);
        }
        // This is certainly not the most efficient implementation, as it involves the construction of up to one new BigDecimal and a BigInteger
        // TODO: replace it by a zero GC version
        // Blame JDK, there is not even a current method to determine if a BigDecimal is integral despite a scale > 0,
        // nor to get its mantissa without creating additional objects.
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).unscaledValue().longValue());
    }

    /** Returns an instance of this class with a specified mantissa. */
    @Override
    public MilliUnits newInstanceOf(final long mantissa) {
        if (mantissa == this.mantissa)
            return this;
        return of(mantissa);
    }

    /** Returns the maximum number of fractional digits of an instance of this class. */
    @Override
    public int scale() {
        return DECIMALS;
    }

    /** Returns the instance of this class which represents the number 0. */
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
    protected MilliUnits getMyself() {
        return this;
    }

    /** Used by serialization code of the bonaparte adapters, to avoid separate adapter classes. */
    public long marshal() {
        return mantissa;
    }

    /** Used by deserialization code of the bonaparte adapters, to avoid separate adapter classes. */
    public static MilliUnits unmarshal(final Long mantissa) {
        return mantissa == null ? null : of(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
