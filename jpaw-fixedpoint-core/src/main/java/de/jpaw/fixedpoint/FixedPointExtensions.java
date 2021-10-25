package de.jpaw.fixedpoint;

import java.math.RoundingMode;

import de.jpaw.fixedpoint.types.FemtoUnits;
import de.jpaw.fixedpoint.types.Hundreds;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.NanoUnits;
import de.jpaw.fixedpoint.types.PicoUnits;
import de.jpaw.fixedpoint.types.Tenths;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.fixedpoint.types.VariableUnits;

/** Provides extension methods / syntax sugar for Xtend. */
public final class FixedPointExtensions {

    private FixedPointExtensions() { }

    // suffix-like methods, as Xtend syntax sugar (16.millis)
    public static Units units(final long a) {
        return Units.of(a);
    }

    public static Tenths tenths(final long a) {
        return Tenths.of(a);
    }

    public static Hundreds hundreds(final long a) {
        return Hundreds.of(a);
    }

    public static MilliUnits millis(final long a) {
        return MilliUnits.of(a);
    }

    public static MicroUnits micros(final long a) {
        return MicroUnits.of(a);
    }

    public static NanoUnits nanos(final long a) {
        return NanoUnits.of(a);
    }

    public static PicoUnits picos(final long a) {
        return PicoUnits.of(a);
    }

    public static FemtoUnits femtos(final long a) {
        return FemtoUnits.of(a);
    }

    // type casts
    public static Units asUnits(final FixedPointBase<?> a) {
        return Units.of(a);
    }

    public static MilliUnits asMillis(final FixedPointBase<?> a) {
        return MilliUnits.of(a);
    }

    public static MicroUnits asMicros(final FixedPointBase<?> a) {
        return MicroUnits.of(a);
    }

    public static NanoUnits asNanos(final FixedPointBase<?> a) {
        return NanoUnits.of(a);
    }

    public static PicoUnits asPicos(final FixedPointBase<?> a) {
        return PicoUnits.of(a);
    }

    public static FemtoUnits asFemtos(final FixedPointBase<?> a) {
        return FemtoUnits.of(a);
    }

    public static VariableUnits asVariable(final FixedPointBase<?> a) {
        return VariableUnits.of(a);
    }

    // percent is a synonym to hundreds, when applied to an integer
    public static Hundreds percent(final long a) {
        return Hundreds.of(a);
    }


    // type conversions with possible scale
    public static Units asUnits(final FixedPointBase<?> a, final RoundingMode rounding) {
        return Units.of(a, rounding);
    }

    public static MilliUnits asMillis(final FixedPointBase<?> a, final RoundingMode rounding) {
        return MilliUnits.of(a, rounding);
    }

    public static MicroUnits asMicros(final FixedPointBase<?> a, final RoundingMode rounding) {
        return MicroUnits.of(a, rounding);
    }

    public static NanoUnits asNanos(final FixedPointBase<?> a, final RoundingMode rounding) {
        return NanoUnits.of(a, rounding);
    }

    public static PicoUnits asPicos(final FixedPointBase<?> a, final RoundingMode rounding) {
        return PicoUnits.of(a, rounding);
    }

    public static FemtoUnits asFemtos(final FixedPointBase<?> a, final RoundingMode rounding) {
        return FemtoUnits.of(a, rounding);
    }

    public static VariableUnits ofScale(final long a, final int scale) {
        return VariableUnits.valueOf(a, scale);
    }

    public static FixedPointBase<?> gsum(final Iterable<FixedPointBase<?>> iterable) {
        FixedPointBase<?> sum = Units.ZERO;
        for (final FixedPointBase<?> a : iterable) {
            sum = sum.gadd(a);
        }
        return sum;
    }

    // sum iterable extension. Attn! Due to the unknown type, for an empty iterator, null is returned.
    // altering this would need to define it per subtype, but then it cannot be applied to generic types any more.
    public static <CLASS extends FixedPointBase<CLASS>> CLASS sum(final Iterable<CLASS> iterable) {
        CLASS sum = null;
        for (final CLASS a : iterable) {
            sum = sum != null ? sum.add(a) : a;
        }
        return sum;
    }

    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_plus(final CLASS a, final CLASS b) {
        return a.add(b);
    }
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(final CLASS a, final CLASS b) {
        return a.subtract(b);
    }
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_multiply(final CLASS a, final int b) {
        return a.multiply(b);
    }
    /** Xtend syntax sugar. unary minus maps to the negate method. */
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(final CLASS a) {
        return a.negate();
    }
    /** Xtend syntax sugar. not maps to the isZero method. */
    public static <CLASS extends FixedPointBase<CLASS>> boolean operator_not(final CLASS a) {
        return a.getMantissa() == 0;
    }

    // != and == would make sense here if left null should be supported
//    public static boolean operator_equals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b == null;
//        else
//            return a.equals(b);
//    }
//    public static boolean operator_notEquals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b != null;
//        else
//            return !a.equals(b);
//    }

}
