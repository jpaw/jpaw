package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import de.jpaw.fixedpoint.FixedPointBase;

public class VariableUnits extends FixedPointBase<VariableUnits> {
    private static final long serialVersionUID = 8621674182590849295L;
    private final int scale;
    private final static VariableUnits [] ZEROs = {
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
    private final static VariableUnits [] ONEs = {
            new VariableUnits(powersOfTen[0], 0),
            new VariableUnits(powersOfTen[1], 1),
            new VariableUnits(powersOfTen[2], 2),
            new VariableUnits(powersOfTen[3], 3),
            new VariableUnits(powersOfTen[4], 4),
            new VariableUnits(powersOfTen[5], 5),
            new VariableUnits(powersOfTen[6], 6),
            new VariableUnits(powersOfTen[7], 7),
            new VariableUnits(powersOfTen[8], 8),
            new VariableUnits(powersOfTen[9], 9),
            new VariableUnits(powersOfTen[10], 10),
            new VariableUnits(powersOfTen[11], 11),
            new VariableUnits(powersOfTen[12], 12),
            new VariableUnits(powersOfTen[13], 13),
            new VariableUnits(powersOfTen[14], 14),
            new VariableUnits(powersOfTen[15], 15),
            new VariableUnits(powersOfTen[16], 16),
            new VariableUnits(powersOfTen[17], 17),
            new VariableUnits(powersOfTen[18], 18)
    };

    public final static int scaleCheck(int scale) {
        if (scale < 0 || scale > 18)
            throw new IllegalArgumentException("Illegal scale " + scale + ", must be in range [0,18]");
        return scale;
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static VariableUnits of(long mantissa, int scale) {
        return ZEROs[scale].newInstanceOf(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static VariableUnits valueOf(long value) {
        return ZEROs[0].newInstanceOf(value);
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static VariableUnits of(FixedPointBase<?> that) {
        return ZEROs[that.scale()].newInstanceOf(that.getMantissa());
    }

    /** Returns a re-typed instance of that. SAME AS THE PREVIOUS METHOD, provided for symmetry. */
    public static VariableUnits of(FixedPointBase<?> that, RoundingMode rounding) {
        return ZEROs[that.scale()].newInstanceOf(that.getMantissa());
    }

    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static VariableUnits valueOf(BigDecimal number, int scale) {
        return valueOf(number.setScale(scale, RoundingMode.UNNECESSARY).scaleByPowerOfTen(scale).longValue(), scale);
    }

    /** Subroutine for valueOf(String) and String constructor, to define the desired number of digits. */
    static private final int parseTargetScale(String src) {
        int indexOfDecimalPoint = src.indexOf('.');
        return indexOfDecimalPoint < 0 ? 0 : src.length() - indexOfDecimalPoint - 1;
    }

    /** Factory method. Similar to the constructor, but returns cached instances for 0 and 1. */
    public static VariableUnits valueOf(long mantissa, int scale) {
        scaleCheck(scale);
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == powersOfTen[scale])
            return ONEs[scale];
        return new VariableUnits(mantissa, scale);
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static VariableUnits valueOf(String value) {
        int newScale = scaleCheck(parseTargetScale(value));
        return ZEROs[newScale].newInstanceOf(parseMantissa(value, newScale));
    }


    public VariableUnits(long mantissa, int scale) {
        super(mantissa);
        this.scale = scaleCheck(scale);
    }

    public VariableUnits(String value) {
        super(parseMantissa(value, scaleCheck(parseTargetScale(value))));
        scale = parseTargetScale(value);    // redundant computation of scale required due to Java's initialization requirements?
    }


    @Override
    public VariableUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == powersOfTen[scale])
            return ONEs[scale];
        if (mantissa == getMantissa())
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
        return powersOfTen[scale];
    }

    @Override
    public VariableUnits getMyself() {
        return this;
    }

    @Override
    public boolean isFixedScale() {
        return false;  // this implementations carries the scale per instance
    }

    /** Create a new VariableUnits instance as the sum of the provided generic FixedPoint numbers. */
    public static VariableUnits sumOf(List<? extends FixedPointBase<?>> components, boolean addOne) {
        int maxScale = 0;
        for (FixedPointBase<?> e : components)
            if (e.scale() > maxScale)
                maxScale = e.scale();
        long sum = addOne ? powersOfTen[maxScale] : 0;
        for (FixedPointBase<?> e : components)
            sum += e.getMantissa() * powersOfTen[maxScale - e.scale()];
        return VariableUnits.of(sum, maxScale);
    }

    @Override
    public double getScaleAsDouble() {
        return 1.0 / (double)powersOfTen[scale];
    }
}
