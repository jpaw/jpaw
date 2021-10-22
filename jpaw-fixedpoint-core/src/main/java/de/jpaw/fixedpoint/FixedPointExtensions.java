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
    public static Units units(long a) {
        return Units.of(a);
    }

    public static Tenths tenths(long a) {
        return Tenths.of(a);
    }

    public static Hundreds hundreds(long a) {
        return Hundreds.of(a);
    }

    public static MilliUnits millis(long a) {
        return MilliUnits.of(a);
    }

    public static MicroUnits micros(long a) {
        return MicroUnits.of(a);
    }

    public static NanoUnits nanos(long a) {
        return NanoUnits.of(a);
    }

    public static PicoUnits picos(long a) {
        return PicoUnits.of(a);
    }

    public static FemtoUnits femtos(long a) {
        return FemtoUnits.of(a);
    }

    // type casts
    public static Units asUnits(FixedPointBase<?> a) {
        return Units.of(a);
    }

    public static MilliUnits asMillis(FixedPointBase<?> a) {
        return MilliUnits.of(a);
    }

    public static MicroUnits asMicros(FixedPointBase<?> a) {
        return MicroUnits.of(a);
    }

    public static NanoUnits asNanos(FixedPointBase<?> a) {
        return NanoUnits.of(a);
    }

    public static PicoUnits asPicos(FixedPointBase<?> a) {
        return PicoUnits.of(a);
    }

    public static FemtoUnits asFemtos(FixedPointBase<?> a) {
        return FemtoUnits.of(a);
    }

    public static VariableUnits asVariable(FixedPointBase<?> a) {
        return VariableUnits.of(a);
    }

    // percent is a synonym to hundreds, when applied to an integer
    public static Hundreds percent(long a) {
        return Hundreds.of(a);
    }


    // type conversions with possible scale
    public static Units asUnits(FixedPointBase<?> a, RoundingMode rounding) {
        return Units.of(a, rounding);
    }

    public static MilliUnits asMillis(FixedPointBase<?> a, RoundingMode rounding) {
        return MilliUnits.of(a, rounding);
    }

    public static MicroUnits asMicros(FixedPointBase<?> a, RoundingMode rounding) {
        return MicroUnits.of(a, rounding);
    }

    public static NanoUnits asNanos(FixedPointBase<?> a, RoundingMode rounding) {
        return NanoUnits.of(a, rounding);
    }

    public static PicoUnits asPicos(FixedPointBase<?> a, RoundingMode rounding) {
        return PicoUnits.of(a, rounding);
    }

    public static FemtoUnits asFemtos(FixedPointBase<?> a, RoundingMode rounding) {
        return FemtoUnits.of(a, rounding);
    }

    public static VariableUnits ofScale(long a, int scale) {
        return VariableUnits.valueOf(a, scale);
    }

    public static FixedPointBase<?> gsum(Iterable<FixedPointBase<?>> iterable) {
        FixedPointBase<?> sum = Units.ZERO;
        for (FixedPointBase<?> a : iterable) {
            sum = sum.gadd(a);
        }
        return sum;
    }

    // sum iterable extension. Attn! Due to the unknown type, for an empty iterator, null is returned.
    // altering this would need to define it per subtype, but then it cannot be applied to generic types any more.
    public static <CLASS extends FixedPointBase<CLASS>> CLASS sum(Iterable<CLASS> iterable) {
        CLASS sum = null;
        for (CLASS a : iterable) {
            sum = sum != null ? sum.add(a) : a;
        }
        return sum;
    }

    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_plus(CLASS a, CLASS b) {
        return a.add(b);
    }
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(CLASS a, CLASS b) {
        return a.subtract(b);
    }
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_multiply(CLASS a, int b) {
        return a.multiply(b);
    }
    /** Xtend syntax sugar. unary minus maps to the negate method. */
    public static <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(CLASS a) {
        return a.negate();
    }
    /** Xtend syntax sugar. not maps to the isZero method. */
    public static <CLASS extends FixedPointBase<CLASS>> boolean operator_not(CLASS a) {
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
