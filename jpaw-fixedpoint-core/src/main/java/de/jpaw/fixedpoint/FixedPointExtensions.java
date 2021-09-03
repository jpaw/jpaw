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
public class FixedPointExtensions {

    // suffix-like methods, as Xtend syntax sugar (16.millis)
    static public Units units(long a) {
        return Units.of(a);
    }

    static public Tenths tenths(long a) {
        return Tenths.of(a);
    }

    static public Hundreds hundreds(long a) {
        return Hundreds.of(a);
    }

    static public MilliUnits millis(long a) {
        return MilliUnits.of(a);
    }

    static public MicroUnits micros(long a) {
        return MicroUnits.of(a);
    }

    static public NanoUnits nanos(long a) {
        return NanoUnits.of(a);
    }

    static public PicoUnits picos(long a) {
        return PicoUnits.of(a);
    }

    static public FemtoUnits femtos(long a) {
        return FemtoUnits.of(a);
    }

    // type casts
    static public Units asUnits(FixedPointBase<?> a) {
        return Units.of(a);
    }

    static public MilliUnits asMillis(FixedPointBase<?> a) {
        return MilliUnits.of(a);
    }

    static public MicroUnits asMicros(FixedPointBase<?> a) {
        return MicroUnits.of(a);
    }

    static public NanoUnits asNanos(FixedPointBase<?> a) {
        return NanoUnits.of(a);
    }

    static public PicoUnits asPicos(FixedPointBase<?> a) {
        return PicoUnits.of(a);
    }

    static public FemtoUnits asFemtos(FixedPointBase<?> a) {
        return FemtoUnits.of(a);
    }

    static public VariableUnits asVariable(FixedPointBase<?> a) {
        return VariableUnits.of(a);
    }

    // percent is a synonym to hundreds, when applied to an integer
    static public Hundreds percent(long a) {
        return Hundreds.of(a);
    }


    // type conversions with possible scale
    static public Units asUnits(FixedPointBase<?> a, RoundingMode rounding) {
        return Units.of(a, rounding);
    }

    static public MilliUnits asMillis(FixedPointBase<?> a, RoundingMode rounding) {
        return MilliUnits.of(a, rounding);
    }

    static public MicroUnits asMicros(FixedPointBase<?> a, RoundingMode rounding) {
        return MicroUnits.of(a, rounding);
    }

    static public NanoUnits asNanos(FixedPointBase<?> a, RoundingMode rounding) {
        return NanoUnits.of(a, rounding);
    }

    static public PicoUnits asPicos(FixedPointBase<?> a, RoundingMode rounding) {
        return PicoUnits.of(a, rounding);
    }

    static public FemtoUnits asFemtos(FixedPointBase<?> a, RoundingMode rounding) {
        return FemtoUnits.of(a, rounding);
    }

    static public VariableUnits ofScale(long a, int scale) {
        return new VariableUnits(a, scale);
    }

    static public FixedPointBase<?> gsum(Iterable<FixedPointBase<?>> iterable) {
        FixedPointBase<?> sum = Units.ZERO;
        for (FixedPointBase<?> a : iterable) {
            sum = sum.gadd(a);
        }
        return sum;
    }

    // sum iterable extension. Attn! Due to the unknown type, for an empty iterator, null is returned.
    // altering this would need to define it per subtype, but then it cannot be applied to generic types any more.
    static public <CLASS extends FixedPointBase<CLASS>> CLASS sum(Iterable<CLASS> iterable) {
        CLASS sum = null;
        for (CLASS a : iterable) {
            sum = sum != null ? sum.add(a) : a;
        }
        return sum;
    }

    static public <CLASS extends FixedPointBase<CLASS>> CLASS operator_plus(CLASS a, CLASS b) {
        return a.add(b);
    }
    static public <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(CLASS a, CLASS b) {
        return a.subtract(b);
    }
    static public <CLASS extends FixedPointBase<CLASS>> CLASS operator_multiply(CLASS a, int b) {
        return a.multiply(b);
    }
    /** Xtend syntax sugar. unary minus maps to the negate method. */
    static public <CLASS extends FixedPointBase<CLASS>> CLASS operator_minus(CLASS a) {
        return a.negate();
    }
    /** Xtend syntax sugar. not maps to the isZero method. */
    static public <CLASS extends FixedPointBase<CLASS>> boolean operator_not(CLASS a) {
        return a.getMantissa() == 0;
    }

    // != and == would make sense here if left null should be supported
//    static public boolean operator_equals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b == null;
//        else
//            return a.equals(b);
//    }
//    static public boolean operator_notEquals(FixedPointBase a, FixedPointBase b) {
//        if (a == null)
//            return b != null;
//        else
//            return !a.equals(b);
//    }

}
