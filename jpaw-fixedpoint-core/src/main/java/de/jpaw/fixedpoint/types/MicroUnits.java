package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

/** Instances of this class represent numbers with a fixed precision of 6 decimals, and up to 18 total digits precision. */
public class MicroUnits extends FixedPointBase<MicroUnits> {
    private static final long serialVersionUID = -466464673376366006L;
    private static final int DECIMALS = 6;
    private static final long UNIT_MANTISSA = 1_000_000L;
    private static final double UNIT_SCALE = UNIT_MANTISSA;       // cast to double at class initialization time
    private static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division

    /** The representation of the number 0 in this class. This implementation attempts to maintain a single instance of 0 only. */
    public static final MicroUnits ZERO = new MicroUnits(0);

    /** The representation of the number 1 in this class. This implementation attempts to maintain a single instance of 0 only. */
    public static final MicroUnits ONE = new MicroUnits(UNIT_MANTISSA);

    // external callers use valueOf factory method, which returns existing objects for 0 and 1. This constructor is used by the factory methods
    private MicroUnits(long mantissa) {
        super(mantissa);
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static MicroUnits of(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        return new MicroUnits(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static MicroUnits valueOf(long value) {
        return of(value * UNIT_MANTISSA);
    }

    /** Constructs an instance with a value specified via a parameter of type double. */
    public static MicroUnits valueOf(double value) {
        return of(Math.round(value * UNIT_SCALE));
    }

    /** Constructs an instance with a value specified via string representation. */
    public static MicroUnits valueOf(String value) {
        return of(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of another fixed point type. Loosing precision is not supported. */
    public static MicroUnits of(FixedPointBase<?> that) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MicroUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static MicroUnits of(FixedPointBase<?> that, RoundingMode rounding) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return MicroUnits.of(that.getMantissa() * powersOfTen[scaleDiff]);
        // rescale
        return  MicroUnits.of(divide_longs(that.getMantissa(), powersOfTen[-scaleDiff], rounding));
    }

    /** Constructs an instance with a value specified via a parameter of type <code>BigDecimal</code>.
     * Deprecated. Use valueOf() instead. */
    @Deprecated
    public static MicroUnits of(BigDecimal number) {
        return valueOf(number);
    }

    /** Constructs an instance with a value specified via a parameter of type <code>BigDecimal</code>. */
    public static MicroUnits valueOf(BigDecimal number) {
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

    /** Returns an instance of this class with a specified mantissa. */
    @Override
    public MicroUnits newInstanceOf(long mantissa) {
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
    public MicroUnits getZero() {
        return ZERO;
    }

    @Override
    public MicroUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

    @Override
    protected MicroUnits getMyself() {
        return this;
    }

    /** Used by serialization code of the bonaparte adapters, to avoid separate adapter classes. */
    public long marshal() {
        return mantissa;
    }

    /** Used by deserialization code of the bonaparte adapters, to avoid separate adapter classes. */
    public static MicroUnits unmarshal(Long mantissa) {
        return mantissa == null ? null : of(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
