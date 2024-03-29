package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import de.jpaw.fixedpoint.FixedPointBase;

public final class VariableUnits extends FixedPointBase<VariableUnits> {
    private static final long serialVersionUID = 8621674182590849295L;
    private final int scale;
    private static final VariableUnits[] ZEROs = {
            new VariableUnits(0L, 0),
            new VariableUnits(0L, 1),
            new VariableUnits(0L, 2),
            new VariableUnits(0L, 3),
            new VariableUnits(0L, 4),
            new VariableUnits(0L, 5),
            new VariableUnits(0L, 6),
            new VariableUnits(0L, 7),
            new VariableUnits(0L, 8),
            new VariableUnits(0L, 9),
            new VariableUnits(0L, 10),
            new VariableUnits(0L, 11),
            new VariableUnits(0L, 12),
            new VariableUnits(0L, 13),
            new VariableUnits(0L, 14),
            new VariableUnits(0L, 15),
            new VariableUnits(0L, 16),
            new VariableUnits(0L, 17),
            new VariableUnits(0L, 18)
    };
    private static final VariableUnits[] ONEs = {
            new VariableUnits(POWERS_OF_TEN[0], 0),
            new VariableUnits(POWERS_OF_TEN[1], 1),
            new VariableUnits(POWERS_OF_TEN[2], 2),
            new VariableUnits(POWERS_OF_TEN[3], 3),
            new VariableUnits(POWERS_OF_TEN[4], 4),
            new VariableUnits(POWERS_OF_TEN[5], 5),
            new VariableUnits(POWERS_OF_TEN[6], 6),
            new VariableUnits(POWERS_OF_TEN[7], 7),
            new VariableUnits(POWERS_OF_TEN[8], 8),
            new VariableUnits(POWERS_OF_TEN[9], 9),
            new VariableUnits(POWERS_OF_TEN[10], 10),
            new VariableUnits(POWERS_OF_TEN[11], 11),
            new VariableUnits(POWERS_OF_TEN[12], 12),
            new VariableUnits(POWERS_OF_TEN[13], 13),
            new VariableUnits(POWERS_OF_TEN[14], 14),
            new VariableUnits(POWERS_OF_TEN[15], 15),
            new VariableUnits(POWERS_OF_TEN[16], 16),
            new VariableUnits(POWERS_OF_TEN[17], 17),
            new VariableUnits(POWERS_OF_TEN[18], 18)
    };

    public static int scaleCheck(final int scale) {
        if (scale < 0 || scale > 18)
            throw new IllegalArgumentException("Illegal scale " + scale + ", must be in range [0,18]");
        return scale;
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static VariableUnits of(final long mantissa, final int scale) {
        return ZEROs[scale].newInstanceOf(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static VariableUnits valueOf(final long value) {
        return ZEROs[0].newInstanceOf(value);
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static VariableUnits of(final FixedPointBase<?> that) {
        return ZEROs[that.scale()].newInstanceOf(that.getMantissa());
    }

    /** Returns a re-typed instance of that. SAME AS THE PREVIOUS METHOD, provided for symmetry. */
    public static VariableUnits of(final FixedPointBase<?> that, final RoundingMode rounding) {
        return ZEROs[that.scale()].newInstanceOf(that.getMantissa());
    }

    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static VariableUnits valueOf(final BigDecimal number, final int scale) {
        return valueOf(number.setScale(scale, RoundingMode.UNNECESSARY).scaleByPowerOfTen(scale).longValue(), scale);
    }

    /** Subroutine for valueOf(String) and String constructor, to define the desired number of digits. */
    private static int parseTargetScale(final String src) {
        final int indexOfDecimalPoint = src.indexOf('.');
        return indexOfDecimalPoint < 0 ? 0 : src.length() - indexOfDecimalPoint - 1;
    }

    /** Factory method. Similar to the constructor, but returns cached instances for 0 and 1. */
    public static VariableUnits valueOf(final long mantissa, final int scale) {
        scaleCheck(scale);
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == POWERS_OF_TEN[scale])
            return ONEs[scale];
        return new VariableUnits(mantissa, scale);
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static VariableUnits valueOf(final String value) {
        final int newScale = scaleCheck(parseTargetScale(value));
        return ZEROs[newScale].newInstanceOf(parseMantissa(value, newScale));
    }

    private VariableUnits(final long mantissa, final int scale) {
        super(mantissa);
        this.scale = scaleCheck(scale);
    }

    public static VariableUnits parse(final String value) {
        final int _scale = parseTargetScale(value);
        final long _mantissa = parseMantissa(value, scaleCheck(_scale));
        return valueOf(_mantissa, _scale);
    }

    @Override
    public VariableUnits newInstanceOf(final long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == POWERS_OF_TEN[scale])
            return ONEs[scale];
        if (mantissa == this.mantissa)
            return this;
        return new VariableUnits(mantissa, scale);
    }

    @Override
    public int scale() {
        return scale;
    }

    @Override
    public VariableUnits getZero() {
        return ZEROs[scale];
    }

    @Override
    public VariableUnits getUnit() {
        return ONEs[scale];
    }

    @Override
    public long getUnitAsLong() {
        return POWERS_OF_TEN[scale];
    }

    @Override
    public VariableUnits getMyself() {
        return this;
    }

    @Override
    public boolean isFixedScale() {
        return false;  // this implementations carries the scale per instance
    }

    /** Adds two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    @Override
    public VariableUnits add(final VariableUnits that) {
        final int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + POWERS_OF_TEN[diff] * that.mantissa);
        else
            return that.newInstanceOf(that.mantissa + POWERS_OF_TEN[-diff] * this.mantissa);
    }

    /** Subtracts two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    @Override
    public VariableUnits subtract(final VariableUnits that) {
        if (that.mantissa == 0L) {
            return getMyself();
        }
        // first checks, if we can void adding the numbers and return either operand.
        final int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - POWERS_OF_TEN[diff] * that.mantissa);
        else
            return that.newInstanceOf(-that.mantissa + POWERS_OF_TEN[-diff] * this.mantissa);
    }


    /** Create a new VariableUnits instance as the sum of the provided generic FixedPoint numbers. */
    public static VariableUnits sumOf(final List<? extends FixedPointBase<?>> components, final boolean addOne) {
        int maxScale = 0;
        for (final FixedPointBase<?> e : components) {
            if (e.scale() > maxScale)
                maxScale = e.scale();
        }
        long sum = addOne ? POWERS_OF_TEN[maxScale] : 0;
        for (final FixedPointBase<?> e : components) {
            sum += e.getMantissa() * POWERS_OF_TEN[maxScale - e.scale()];
        }
        return VariableUnits.of(sum, maxScale);
    }

    @Override
    public double getScaleAsDouble() {
        return 1.0 / POWERS_OF_TEN[scale];
    }
}
