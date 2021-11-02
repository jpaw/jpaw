package de.jpaw.fixedpoint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.fixedpoint.types.VariableUnits;

/**
 * Base class for fixed point arithmetic, using an implicitly scaled long value.
 * There are subclasses per number of decimals (from 0 to 15, Units to FemtoUnits), and a variable scale
 * class, which stores the scale in a separate instance variable.
 *
 * Instances of this class are immutable.
 *
 * @author Michael Bischoff
 *
 */
public abstract class FixedPointBase<CLASS extends FixedPointBase<CLASS>> extends Number implements Serializable, Comparable<FixedPointBase<?>> {
    private static final long serialVersionUID = 8834214052987561284L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FixedPointBase.class);

    /**
     * Fixed point types by default suppress any trailing decimal zeros with the <code>toString()</code> method.
     * Set this to false to get a behavior similar to <code>BigDecimal</code>, always printing the <code>scale</code> number of fractional digits.
     **/
    public static boolean outputToStringMinimized = true;  // if false, all decimals will be printed

    /** Map to convert rounding mode for negated numbers. */
    private static final EnumMap<RoundingMode, RoundingMode> ROUNDING_MODE_MAPPING = new EnumMap<>(RoundingMode.class);
    static {
        ROUNDING_MODE_MAPPING.put(RoundingMode.UNNECESSARY, RoundingMode.UNNECESSARY);
        ROUNDING_MODE_MAPPING.put(RoundingMode.FLOOR,     RoundingMode.UP);
        ROUNDING_MODE_MAPPING.put(RoundingMode.CEILING,   RoundingMode.DOWN);
        ROUNDING_MODE_MAPPING.put(RoundingMode.UP,        RoundingMode.UP);  // always away from zero
        ROUNDING_MODE_MAPPING.put(RoundingMode.DOWN,      RoundingMode.DOWN);
        ROUNDING_MODE_MAPPING.put(RoundingMode.HALF_UP,   RoundingMode.HALF_UP);
        ROUNDING_MODE_MAPPING.put(RoundingMode.HALF_DOWN, RoundingMode.HALF_DOWN);
        ROUNDING_MODE_MAPPING.put(RoundingMode.HALF_EVEN, RoundingMode.HALF_EVEN);
    }

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final int[] INT_POWERS_OF_TEN = {
            1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000
    };
    protected static final long[] POWERS_OF_TEN = {
            1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L,
            1000000000000000000L
    };

    public static final long getPowerOfTen(final int scale) {
        return POWERS_OF_TEN[scale];
    }

    private transient String asString = null; // due to efforts to return this for arithmetic operations whereever possible, it is likely that the same number will be printed multiple times, and due to Java object alignments, it does not increase the size of the object
    protected final long mantissa;    // the significant digits

    protected FixedPointBase(final long mantissa) {
        this.mantissa = mantissa;
    }

    /** Returns a fixed point value object which has the same number of decimals as this, with a given mantissa.
     * This implementation returns cached instances for 0 and 1. Otherwise, in case this has the same mantissa, this is returned. */
    public abstract CLASS newInstanceOf(long xmantissa);

    /** Computes the mantissa for a fixed point number of target scale, for a given double. */
    public static long mantissaFor(final double value, final int scale) {
        return Math.round(value * POWERS_OF_TEN[scale]);
    }

    /** Get the number of decimals. */
    public abstract int scale();

    /** Get the number 0 in the same scale. */
    public abstract CLASS getZero();

    /** Get the number 1 in the same scale. */
    public abstract CLASS getUnit();

    /**
     * Get a reference to myself (essentially "this", but avoids a type cast).
     * This is a workaround, required because the compiler currently does not acknowledge that this class is abstract.
     * Invocation is only done from this class (only private callers),
     * but it must be protected because the derived classed have to override it.
     */
    protected abstract CLASS getMyself();

    /** Get the value representing the number 1. */
    public abstract long getUnitAsLong();

    /** Get the scale required to multiply to the mantissa with. */
    public abstract double getScaleAsDouble();

    /** Get the mantissa of this number as a primitive long. */
    public long getMantissa() {
        return mantissa;
    }

    /** Checks that the number is within 18 digits of magnitude. */
    public boolean isWithin18Digits() {
        return -999_999_999_999_999_999L <= mantissa && mantissa <= 999_999_999_999_999_999L;
    }

    /** Checks that the number is within 18 digits of magnitude. */
    public boolean isWithinDigits(final int numberOfDigits) {
        final long oneMore = POWERS_OF_TEN[numberOfDigits];
        return -oneMore < mantissa && mantissa < oneMore;
    }

    /** Returns true if to instances of the same subclass will always have the same number of decimals. */
    public boolean isFixedScale() {
        return true;  // default implementations: most subtypes do.
    }

    /** Returns true if this is an integral number. */
    public boolean isIntegralValue() {
        final long scale = POWERS_OF_TEN[scale()];
        return mantissa % scale == 0;
    }

    /**
     * Returns the integral part of the number, as fixed point data type.
     * If you need it as a long, use the longValue() method (with a possible offset of 1 for negative numbers).
     */
    public CLASS floor() {
        final long scale = POWERS_OF_TEN[scale()];
        final long fraction = mantissa % scale;
        if (fraction == 0) {
           return getMyself();
        }
        return newInstanceOf(mantissa < 0 ? mantissa - fraction - getUnitAsLong() : mantissa - fraction);
    }

    /**
     * Returns the integral part of the number, as fixed point data type.
     * If you need it as a long, use the longValue() method (with a possible offset of 1 for positive numbers).
     */
    public CLASS ceil() {
        final long scale = POWERS_OF_TEN[scale()];
        final long fraction = mantissa % scale;
        if (fraction == 0) {
           return getMyself();
        }
        return newInstanceOf(mantissa < 0 ? mantissa - fraction : mantissa - fraction + getUnitAsLong());
    }

    public long fraction() {
        final long scale = POWERS_OF_TEN[scale()];
        final long integralDigits = mantissa / scale;
        return Math.abs(mantissa - integralDigits * scale);
    }

    // only called for digits != 0 && scale > 0
    private static void appendFraction(final StringBuilder sb, int scale, int digits, final int minDigits) {
        final int maxScaleLeft = scale - minDigits;
        do {
            final int nextPower = INT_POWERS_OF_TEN[--scale];
            final int nextDigit = digits / nextPower;
            sb.append(DIGITS[nextDigit]);
            digits -= nextDigit * nextPower;
        } while (digits != 0);  // replace condition by 'scale > 0' to get full length of fractional digits
        while (scale > maxScaleLeft) {
            sb.append('0');
            --scale;
        }
    }
    private static void appendFraction(final StringBuilder sb, int scale, long digits, final int minDigits) {
        final int maxScaleLeft = scale - minDigits;
        do {
            final long nextPower = POWERS_OF_TEN[--scale];
            final long nextDigit = digits / nextPower;
            sb.append(DIGITS[(int) nextDigit]);
            digits -= nextDigit * nextPower;
        } while (digits != 0L);  // replace condition by 'scale > 0' to get full length of fractional digits
        while (scale > maxScaleLeft) {
            sb.append('0');
            --scale;
        }
    }

    /** Appends a separately provided mantissa in a human readable form to the provided StringBuilder, based on settings of a reference number (this).
     * Method is also used by external classes. */
    public static void append(final StringBuilder sb, final long mantissa, final int scale) {
        append(sb, mantissa, scale, 0);
    }
    public static void append(final StringBuilder sb, long mantissa, final int scale, final int minDigits) {
        // straightforward implementation discarded due to too much GC overhead (construction of a temporary BigDecimal)
        // return BigDecimal.valueOf(mantissa, scale()).toPlainString();
        // version with double not considered due to precision loss (mantissa of a double is just 15 digits, we want 18)
        if (scale == 0) {
            sb.append(mantissa);
        } else {
            if (mantissa < 0) {
                // this code is required to ensure output of sign for negative numbers between -1 and 0 (integral portion 0).
                sb.append('-');
                mantissa = -mantissa;
            }
            // separate the digits in a way that the fractional ones are not negative
            final long ten2scale = POWERS_OF_TEN[scale];
            final long integralDigits = mantissa / ten2scale;
            final long decimalDigits = Math.abs(mantissa - integralDigits * ten2scale);
            sb.append(integralDigits);
            // conditional append of fractional part
            if (minDigits > 0 || decimalDigits != 0L) {
                sb.append('.');
                if (decimalDigits <= 999_999_999) {
                    // max 9 digits: do it with integers, to avoid costly 6 bit divisions
                    appendFraction(sb, scale, (int)decimalDigits, minDigits);
                } else {
                    appendFraction(sb, scale, decimalDigits, minDigits);
                }
            }
        }
    }

    /**
     * Returns the value in a human readable form.
     *
     * The same notes as for BigDecimal.toString() apply:
     * <ul>
     * <li>There is a one-to-one mapping between the distinguishable VariableUnits values and the result of this conversion.
     *     That is, every distinguishable VariableUnits value (unscaled value and scale) has a unique string representation
     *     as a result of using toString. If that string representation is converted back to a VariableUnits
     *     using the VariableUnits(String) constructor, then the original value will be recovered.</li>
     * <li>The string produced for a given number is always the same; it is not affected by locale.
     *     This means that it can be used as a canonical string representation for exchanging decimal data,
     *     or as a key for a Hashtable, etc.
     *     Locale-sensitive number formatting and parsing is handled by the NumberFormat class and its subclasses.</li>
     * </ul>
     * */
    @Override
    public String toString() {
        if (asString == null) {
            // not yet computed
            if (scale() == 0) {
                asString = Long.toString(mantissa);
            } else {
                // we need 21 characters at max (19 digits plus optional sign, plus decimal point), so allocate it with sufficient initial size to avoid realloc
                final StringBuilder sb = new StringBuilder(22);
                append(sb, mantissa, scale(), outputToStringMinimized ? 0 : scale());
                asString = sb.toString();
            }
        }
        return asString;
    }

    public String toString(final boolean minimized) {
        // not yet computed
        if (scale() == 0) {
            return Long.toString(mantissa);
        } else {
            // we need 21 characters at max (19 digits plus optional sign, plus decimal point), so allocate it with sufficient initial size to avoid realloc
            final StringBuilder sb = new StringBuilder(22);
            append(sb, mantissa, scale(), minimized ? 0 : scale());
            return sb.toString();
        }
    }

    public String toString(final int minFractionalDigits) {
        // not yet computed
        if (scale() == 0) {
            return Long.toString(mantissa);
        } else {
            // we need 21 characters at max (19 digits plus optional sign, plus decimal point), so allocate it with sufficient initial size to avoid realloc
            final StringBuilder sb = new StringBuilder(22);
            append(sb, mantissa, scale(), minFractionalDigits);
            return sb.toString();
        }
    }

    /** Parses a string for a maximum number of decimal digits. Extra digits will be ignored as long as they are 0, but
     * an ArithmeticException will be raised if there are more significant digits than allowed, i.e. no rounding is allowed.
     *
     * The method should be final, because it is also used as a constructor subrountine.
     *
     * @param src  - the input string
     * @param targetScale - the number of digits the result will be scaled for.
     * @return the mantissa in the specified scale
     */
    protected static final long parseMantissa(final String src, final int targetScale) {
        final int indexOfDecimalPoint = src.indexOf('.');
        if (indexOfDecimalPoint < 0) {
            // no point included, easy case, integral number
            return Long.parseLong(src) * POWERS_OF_TEN[targetScale];
        } else {
            // parse the integral part, then the fractional part. Support special case to capture -.4 or +.4 (Long.parse("-") won't work!)
            final long integralPart = (
                    indexOfDecimalPoint == 1 && (src.charAt(0) == '-' || src.charAt(0) == '+'))
                    ? 0
                    : Long.parseLong(src.substring(0, indexOfDecimalPoint)) * POWERS_OF_TEN[targetScale];
            final int decimalDigitsFound = src.length() - indexOfDecimalPoint - 1;
            if (decimalDigitsFound == 0) {   // the "1." case => same as integral case
                return integralPart;
            }
            final String fraction = src.substring(indexOfDecimalPoint + 1);
            if (fraction.charAt(0) == '-' || fraction.charAt(0) == '+')
                throw new NumberFormatException("Extra sign found at start of fractional digits");
            long fractionalPart = Long.parseLong(fraction);  // parseUnsignedLong still allows a leading plus sign
            // apply the sign to the fractional part. checking integralPart won't work here, because that may be "-0"
            if (src.charAt(0) == '-')
                fractionalPart = -fractionalPart;
            final int fractionalDigitsDiff = targetScale - fraction.length();
            if (fractionalDigitsDiff >= 0) {
                // no rounding required
                return integralPart + fractionalPart * POWERS_OF_TEN[fractionalDigitsDiff];
            } else {
                // if we have too may significant digits, throw an Exception
                if (fractionalPart % POWERS_OF_TEN[-fractionalDigitsDiff] != 0)
                    throw new NumberFormatException("Too many significant fractional digits specified: " + src + ", allowed: " + targetScale);
                return integralPart + fractionalPart / POWERS_OF_TEN[-fractionalDigitsDiff];
            }
        }
    }

    public static final long mantissaFor(final String src, final int targetScale) {
        return parseMantissa(src, targetScale);
    }

    /**
     * Computes the target mantissa of an existing with currentScale, for a given desired target scale.
     * In case of precision loss, a flag indicates whether that is acceptable.
     */
    public static final long mantissaFor(final long currentMantissa, final int currentScale, final int desiredScale, final boolean allowRounding) {
        if (currentMantissa == 0L) {
            return currentMantissa;
        }
        final int toMultiplyWithExponent = desiredScale - currentScale;
        if (toMultiplyWithExponent >= 0) {
            if (toMultiplyWithExponent > 18) {
                throw new ArithmeticException("Overflow");
            }
            return currentMantissa * POWERS_OF_TEN[toMultiplyWithExponent];
        } else {
            if (!allowRounding) {
                if (toMultiplyWithExponent < -18 || currentMantissa % POWERS_OF_TEN[-toMultiplyWithExponent] != 0L) {
                    throw new ArithmeticException("Rounding required but not allowed");
                }
            }
            if (toMultiplyWithExponent < -18) {
                return 0L; // underflow
            }
            return currentMantissa / POWERS_OF_TEN[-toMultiplyWithExponent];
        }
    }

    @Override
    public int hashCode() {
        return scale() + 19 * (int)(mantissa ^ mantissa >>> 32);
    }

    /** As with BigDecimal, equals returns true only of both objects are identical in all aspects. Use compareTo for numerical identity. */
    @Override
    public boolean equals(final Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;
        final FixedPointBase<?> _that = (FixedPointBase<?>)that;
        return scale() == _that.scale() && mantissa == _that.mantissa && this.getClass() == that.getClass();
    }

    /** Returns the absolute value of this, using the same type and scale. */
    public CLASS abs() {
        if (mantissa >= 0)
            return getMyself();
        return newInstanceOf(-mantissa);
    }

    /** Returns a number with the opposite sign. */
    public CLASS negate() {
        return newInstanceOf(-mantissa);
    }

    /** Returns the signum of this number, -1, 0, or +1. */
    public int signum() {
        return Long.signum(mantissa);
    }

    /** Returns true if this is numerically equivalent to 1. */
    public boolean isOne() {
        return mantissa == getUnitAsLong();
    }

    /** Returns true if this is numerically equivalent to -1. */
    public boolean isMinusOne() {
        return mantissa == -getUnitAsLong();
    }

    /** Returns true if this is not 0. */
    public boolean isNotZero() {
        return mantissa != 0;
    }

    /** Returns true if this is 0. */
    public boolean isZero() {
        return mantissa == 0;
    }

    /** Returns a unit in the last place. */
    public CLASS ulp() {
        return newInstanceOf(1);
    }

    /** Returns the number scaled by 0.01, by playing with the scale (if possible). */
    @Deprecated
    public VariableUnits percent() {
        switch (scale()) {
        case 18:
            return VariableUnits.valueOf(mantissa / 100, 18);
        case 17:
            return VariableUnits.valueOf(mantissa / 10, 18);
        default:  // 0 .. 16 decimals
            return VariableUnits.valueOf(mantissa, scale() + 2);
        }
    }

    /** Returns the signum of this number, -1, 0, or +1.
     * Special care is taken in this implementation to work around any kind of integral overflows. */
    @Override
    public int compareTo(final FixedPointBase<?> that) {
        // first, tackle the case of same scale, which reduces to integer comparison. This is done first, because it should be the most common case
        final int scaleDiff = this.scale() - that.scale();
        if (scaleDiff == 0) {
            // simple: compare the mantissas
            if (this.mantissa == that.mantissa)
                return 0;
            return this.mantissa < that.mantissa ? -1 : 1;
        }

        // next check is on signum only, to avoid incorrect responses due to integral overflow (MIN_VALUE must be < than MAX_VALUE)
        final int signumThis = Long.signum(this.mantissa);
        final int signumThat = Long.signum(that.mantissa);
        if (signumThis != signumThat) {
            // simple case, number differs by sign already
            return signumThis < signumThat ? -1 : 1;
        }
        if (signumThat == 0)
            return 0; // both are 0
        // here, both are either negative or positive
        // medium difficulty: they have the same scale
        // both operands have the same sign, but differ in scaling. Scale down first, and only if the numbers then are the same, scale up
        if (scaleDiff < 0) {
            long diff = mantissa - that.mantissa / POWERS_OF_TEN[-scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa * POWERS_OF_TEN[-scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        } else {
            long diff = mantissa  / POWERS_OF_TEN[scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa - that.mantissa * POWERS_OF_TEN[scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        }
    }
    /** Xtend syntax sugar. spaceship maps to the compareTo method. */
    public int operator_spaceship(final FixedPointBase<?> that) {
        return compareTo(that);
    }
    public final boolean operator_equals(final FixedPointBase<?> that) {
        return compareTo(that) == 0;
    }
    public final boolean operator_notEquals(final FixedPointBase<?> that) {
        return compareTo(that) != 0;
    }
    public final boolean operator_lessThan(final FixedPointBase<?> that) {
        return compareTo(that) < 0;
    }
    public final boolean operator_lessEquals(final FixedPointBase<?> that) {
        return compareTo(that) <= 0;
    }
    public final boolean operator_greaterThan(final FixedPointBase<?> that) {
        return compareTo(that) > 0;
    }
    public final boolean operator_greaterEquals(final FixedPointBase<?> that) {
        return compareTo(that) >= 0;
    }

    /** Returns the smaller of this and the parameter. */
    public CLASS min(final CLASS that) {
        return mantissa <= that.mantissa ? getMyself() : that;
    }

    /** Returns the bigger of this and the parameter. */
    public CLASS max(final CLASS that) {
        return mantissa >= that.mantissa ? getMyself() : that;
    }

    /** Returns the smaller of this and the parameter, allows different type parameters. */
    public FixedPointBase<?> gmin(final FixedPointBase<?> that) {
        return this.compareTo(that) <= 0 ? this : that;
    }

    /** Returns the bigger of this and the parameter, allows different type parameters. */
    public FixedPointBase<?> gmax(final FixedPointBase<?> that) {
        return this.compareTo(that) >= 0 ? this : that;
    }

    /** Multiplies a fixed point number by an integral factor. The scale (and type) of the product is the same as the one of this. */
    public CLASS multiply(final int factor) {
        return newInstanceOf(mantissa * factor);  // newInstanceOf optimizes the cases of factors 0 and 1
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(final int factor) {
        return multiply(factor);
    }

    /** Returns this + 1. */
    public CLASS increment() {
        return newInstanceOf(mantissa + getUnitAsLong());
    }
    /** Xtend syntax sugar. ++ maps to the increment method. */
    public CLASS operator_plusplus() {
        return increment();
    }

    /** Returns this - 1. */
    public CLASS decrement() {
        return newInstanceOf(mantissa - getUnitAsLong());
    }
    /** Xtend syntax sugar. -- maps to the decrement method. */
    public CLASS operator_minusminus() {
        return decrement();
    }

    /** Returns true if this is smaller than that. */
    public boolean isSmallerThan(CLASS that) {
        return mantissa < that.mantissa;
    }

    /** Returns true if this is smaller or equal to that. */
    public boolean isSmallerOrEqual(CLASS that) {
        return mantissa <= that.mantissa;
    }

    /** Returns true if this is greater than that. */
    public boolean isGreaterThan(CLASS that) {
        return mantissa > that.mantissa;
    }

    /** Returns true if this is smaller or equal to that. */
    public boolean isGreaterOrEqual(CLASS that) {
        return mantissa >= that.mantissa;
    }

    /**
     * Returns true if this is equal to that.
     * This is faster than using equals() because it accepts only non null objects of the same class.
     */
    public boolean isEqualTo(CLASS that) {
        return mantissa == that.mantissa;
    }

    /**
     * Returns true if this is not equal to that.
     * This is faster than using equals() because it accepts only non null objects of the same class.
     */
    public boolean isNotEqualTo(CLASS that) {
        return mantissa != that.mantissa;
    }

    /** Subroutine to provide the mantissa of a multiplication. */
    /*
    public long mantissa_of_multiplication(FixedPointBase<?> that, int targetScale, RoundingMode rounding) {
        int digitsToScale = getDecimals() + that.getDecimals() - targetScale;
        long mantissaA = this.mantissa;
        long mantissaB = that.mantissa;
        if (digitsToScale <= 0) {
            // easy, no rounding
            return mantissaA * mantissaB * POWERS_OF_TEN[-digitsToScale];
        }
        long sign = 1;
        if (mantissaA < 0) {
            mantissaA = -mantissaA;
            sign = -1;
        }
        if (mantissaB < 0) {
            mantissaB = -mantissaB;
            sign = -sign;
        }

        long unroundedProduct;
        // see if we can multiply first, then scale, without loosing precision
        if (Long.numberOfLeadingZeros(mantissaA) + Long.numberOfLeadingZeros(mantissaB) >= 65) {
            // both operands are positive and their product is as well
            unroundedProduct = mantissaA * mantissaB;
        } else {
            // as we do not have a true 128 bit multiplication, we first try to shave off any extra powers of ten
            // in chunks of 3, first A, then B
            while (digitsToScale >= 3) {
                if (mantissaA % 1000 == 0) {
                    mantissaA /= 1000;
                    digitsToScale -= 3;
                } else {
                    break;
                }
            }
            while (digitsToScale >= 3) {
                if (mantissaB % 1000 == 0) {
                    mantissaB /= 1000;
                    digitsToScale -= 3;
                } else {
                    break;
                }
            }
            while (digitsToScale > 0) {
                if (mantissaA % 10 == 0) {
                    mantissaA /= 10;
                    --digitsToScale;
                } else {
                    break;
                }
            }
            while (digitsToScale > 0) {
                if (mantissaB % 10 == 0) {
                    mantissaB /= 10;
                    --digitsToScale;
                } else {
                    break;
                }
            }
            if (digitsToScale == 0) {
                // easy, no rounding
                return sign * mantissaA * mantissaB;
            }
            // repeat the digits test
            if (Long.numberOfLeadingZeros(mantissaA) + Long.numberOfLeadingZeros(mantissaB) >= 65) {
                // both operands are positive and their product is as well
                unroundedProduct = mantissaA * mantissaB;
            } else {
                // FIXME
                return 0;
//                throw new ArithmeticException("internal fixable overflow");
            }
        }
        // the rounding, depending on the mode
        long work;
        switch (rounding) {
        case UNNECESSARY:
            if (unroundedProduct % POWERS_OF_TEN[digitsToScale] != 0L)
                throw new ArithmeticException("Rounding required but forbidden, scaling " + unroundedProduct + " by " + digitsToScale + " digits");
            return sign * unroundedProduct % POWERS_OF_TEN[digitsToScale];
        case DOWN:
            return sign * unroundedProduct / POWERS_OF_TEN[digitsToScale];
        case UP:
            work = unroundedProduct % POWERS_OF_TEN[digitsToScale];
            return sign * (unroundedProduct / POWERS_OF_TEN[digitsToScale] + (work != 0 ? 1 : 0));
        case HALF_UP:
            work = unroundedProduct % POWERS_OF_TEN[digitsToScale];
            return sign * (unroundedProduct / POWERS_OF_TEN[digitsToScale] + (work >= (POWERS_OF_TEN[digitsToScale] >> 1) ? 1 : 0));
        case HALF_DOWN:
            work = unroundedProduct % POWERS_OF_TEN[digitsToScale];
            return sign * (unroundedProduct / POWERS_OF_TEN[digitsToScale] + (work > (POWERS_OF_TEN[digitsToScale] >> 1) ? 1 : 0));
        case CEILING:
            work = unroundedProduct % POWERS_OF_TEN[digitsToScale];
            return sign * unroundedProduct / POWERS_OF_TEN[digitsToScale] + (work != 0 ? 1 : 0);
        case FLOOR:
            work = unroundedProduct % POWERS_OF_TEN[digitsToScale];
            return sign * unroundedProduct / POWERS_OF_TEN[digitsToScale] - (sign < 0 && work != 0 ? 1 : 0);
        case HALF_EVEN:
            work = unroundedProduct % (POWERS_OF_TEN[digitsToScale] << 1);
            // round as follows: [0, 0.5] down, (0.5, 1) up, [1, 1.5) down, [1.5, 2) up
            return sign * (unroundedProduct / POWERS_OF_TEN[digitsToScale] + (work >= (POWERS_OF_TEN[digitsToScale] >> 1) ? 1 : 0));
        default:
            return 0;   // FIXME
        }
    }
    */

    /** Short source, but high GC overhead version, as a testing reference. */
    public long mantissa_of_multiplication_using_BD(final FixedPointBase<?> that, final int targetScale, final RoundingMode rounding) {
        final BigDecimal product = BigDecimal.valueOf(this.mantissa, scale()).multiply(BigDecimal.valueOf(that.mantissa, that.scale()));
        final BigDecimal scaledProduct = product.setScale(targetScale, rounding);
        return scaledProduct.scaleByPowerOfTen(targetScale).unscaledValue().longValue();
    }

    /**
     * Use of native code for scaling and rounding, if required.
     * Private method, currently exposed for benchmarking purposes (to avoid effect of GC).
     * */
    public long mantissa_of_multiplication(final FixedPointBase<?> that, final int targetScale, final RoundingMode rounding) {
        final int digitsToScale = scale() + that.scale() - targetScale;
        // This method is called for nonzero operands only. Now test for sign to reduce to unsigned operation. No worries about LONG_MIN overflow because we support up to 18 digits only for the largest mantissa
        boolean negateResult = false;
        final long mantissaA;
        final long mantissaB;
        if (mantissa < 0L) {
            negateResult = true;
            mantissaA = -this.mantissa;
        } else {
            mantissaA = this.mantissa;
        }
        if (that.mantissa < 0L) {
            negateResult = !negateResult;
            mantissaB = -that.mantissa;
        } else {
            mantissaB = that.mantissa;
        }
        final long productAbsolute;
        if (digitsToScale <= 0) {
            // easy, no rounding (but maybe overflow! TODO: check for it!
            productAbsolute = mantissaA * mantissaB * POWERS_OF_TEN[-digitsToScale];
        } else {
            // invoke the computation. If we have changed the sign, adjust the requested rounding mode accordingly.
            // check if we can do it with a long
            if (((mantissaA | mantissaB) & 0xffffffff80000000L) == 0L) {
                // both have 31 bits only
                productAbsolute = roundMantissa(mantissaA * mantissaB, POWERS_OF_TEN[digitsToScale], negateResult ? ROUNDING_MODE_MAPPING.get(rounding) : rounding);
            } else {
                if (Math.multiplyHigh(mantissaA, mantissaB) == 0) {
                    // another chance to do it within a single multiplication - this covers additional asymmetric operands
                    final long prodTmp = mantissaA * mantissaB;
                    if (prodTmp >= 0) {
                        return negateResult ? -prodTmp : prodTmp;
                    }
                    // else fall through and do the complex one
                }
                productAbsolute = FixedPointNative.multiply_and_scale(mantissaA, mantissaB, digitsToScale, negateResult ? ROUNDING_MODE_MAPPING.get(rounding) : rounding);
            }
        }
        return negateResult ? -productAbsolute : productAbsolute;
    }

    private static long roundMantissa(final long in, final long powerOfTen, final RoundingMode roundingMode) {
        final long quot = in / powerOfTen;
        final long remainder = in % powerOfTen;
        if (remainder == 0L) {
            return quot;
        }
        switch (roundingMode) {
        case UP:              // round towards bigger absolute value
        case CEILING:         // round towards bigger numerical value
            return quot + 1L;
        case DOWN:            // round towards smaller absolute value
        case FLOOR:           // round towards smaller numerical value
            return quot;
        case HALF_DOWN:
            if (2 * remainder > powerOfTen)
                return quot + 1L;
            else
                return quot;
        case HALF_EVEN:
            final long dec = 2 * remainder - powerOfTen;
            if (dec > 0) {
                return quot + 1L;
            } else if (dec < 0) {
                return quot;
            } else {
                // exactly in the middle
                return (quot & 1L) == 0L ? quot + 1L : quot;
            }
        case HALF_UP:
            if (2 * remainder >= powerOfTen)
                return quot + 1L;
            else
                return quot;
        case UNNECESSARY:
            throw new ArithmeticException("Rounding required but forbidden by roundingMode parameter");
        default:
            break;
        }
        return quot; // dead code, but Eclipse wants it!
    }

    public CLASS round(final int desiredScale, final RoundingMode rounding) {
        final int power = scale() - desiredScale;
        if (power <= 0 || mantissa == 0L) {
            // already by design
            return getMyself();
        }
        final long div = POWERS_OF_TEN[power];
        final long newMantissa;
        if (mantissa < 0L) {
            newMantissa = -roundMantissa(-mantissa, div, ROUNDING_MODE_MAPPING.get(rounding)) * div;
        } else {
            newMantissa = roundMantissa(mantissa, div, rounding) * div;
        }
        if (newMantissa == mantissa) {
            return getMyself();
        }
        return newInstanceOf(newMantissa);
    }

    /** Divide a / b and round according to specification. Does not need JNI, because we stay in range of a long here. */
    public static long divide_longs(final long a, final long b, final RoundingMode rounding) {
        final long tmp = a / b;
        final long mod = a % b;
        if (mod == 0)
            return tmp;  // no rounding required: same for all modes...

        switch (rounding) {
        case UP:              // round towards bigger absolute value
            return tmp + (a >= 0 ? 1 : -1);
        case DOWN:            // // round towards smaller absolute value
            return tmp;
        case CEILING:         // round towards bigger numerical value
            return a >= 0 ? tmp + 1 : tmp;
        case FLOOR:           // round towards smaller numerical value
            return a < 0 ? tmp - 1 : tmp;
        case HALF_UP:
            if (a >= 0) {
                return mod >= (b >> 1) ? tmp + 1 : tmp;
            } else {
                return mod <= -(b >> 1) ? tmp - 1 : tmp;
            }
        case HALF_DOWN:
            if (a >= 0) {
                return mod > (b >> 1) ? tmp + 1 : tmp;
            } else {
                return mod < -(b >> 1) ? tmp - 1 : tmp;
            }
        case HALF_EVEN:
            if (a >= 0) {
                if (mod > (b >> 1)) {
                    return tmp + 1;
                } else if (mod < (b >> 1)) {
                    return tmp;
                } else {
                    // in this case, the rounding also depends on the last digit of the result. In case of equidistant numbers, it is rounded towards the nearest even number.
                    return tmp + (tmp & 1);
                }
            } else {
                if (mod < -(b >> 1)) {
                    return tmp - 1;
                } else if (mod > -(b >> 1)) {
                    return tmp;
                } else {
                    // in this case, the rounding also depends on the last digit of the result. In case of equidistant numbers, it is rounded towards the nearest even number.
                    return tmp - (tmp & 1);
                }
            }
        case UNNECESSARY:
            throw new ArithmeticException("Rounding required but forbidden by roundingMode parameter");
        default:
            return tmp;
        }
    }

    /** Multiplies a fixed point number by an another one. The type / scale of the result is undefined. */
    public FixedPointBase<?> gmultiply(final FixedPointBase<?> that, final RoundingMode rounding) {
        if (mantissa == 0)
            return this;                // 0 * x = 0
        if (that.mantissa == 0)
            return that;                // x * 0 = 0
        if (isOne())
            return that;                // 1 * x = x
        if (isMinusOne())
            return that.negate();       // -1 * x = -x
        if (that.isOne())
            return this;                // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(mantissa_of_multiplication(that, this.scale(), rounding));
    }
    /** Multiplies a fixed point number by an another one. The type / scale of the result is the same than that of the left operand. */
    public CLASS multiply(final FixedPointBase<?> that, final RoundingMode rounding) {
        if (mantissa == 0 || that.mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return getMyself();         // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(mantissa_of_multiplication(that, this.scale(), rounding));
    }

    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(final FixedPointBase<?> that) {
        return multiply(that, RoundingMode.HALF_EVEN);
    }

    /**
     * Multiplies a fixed point number by an another one. The type of the result is the same than that of the left operand.
     * The scale of the result is also the same as of the left operand, but the result is rounded to fewer digits.
     * This must be performed directly after multiplication, because a two step rounding could return different results:
     * for 0.445: round(2) = 0.45, then round(1) = 0.5, while for 0.445: round(1) = 0.4 */
    public CLASS multiplyAndRound(final FixedPointBase<?> that, final int desiredDecimals, final RoundingMode rounding) {
        if (desiredDecimals > scale() || desiredDecimals < 0) {
            // cannot round to more than what we have
            LOGGER.error("Requested rounding to {} decimals for target data type {}", this.getClass().getSimpleName());
            throw new ArithmeticException("Requested number of digits for rounding not supported by data type");
        }
        if (mantissa == 0 || that.mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        // no tests for second operand equal to one here, due to possible rounding effects
        return newInstanceOf(mantissa_of_multiplication(that, desiredDecimals, rounding) * POWERS_OF_TEN[scale() - desiredDecimals]);
    }



    /** Divides a fixed point number by an another one. The type / scale of the result is the same than that of the left operand. */
    public CLASS divide(final FixedPointBase<?> that, final RoundingMode rounding) {
        if (that.mantissa == 0L) {
            throw new ArithmeticException("Division by 0");
        }
        if (mantissa == 0)
            return getZero();           // 0 / x = 0
        if (that.isOne())
            return getMyself();         // x / 1 = x
        if (that.isMinusOne())
            return this.negate();       // x / -1 = -x
        return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, that.scale(), that.mantissa, rounding));
    }

    /** Divides a fixed point number by an another one, rounding to decimals digits. The type of the result is the same than that of the left operand. */
    public CLASS divideAndRound(final FixedPointBase<?> that, final int desiredDecimals, final RoundingMode rounding) {
        if (that.mantissa == 0L) {
            throw new ArithmeticException("Division by 0");
        }
        if (desiredDecimals > scale() || desiredDecimals < 0) {
            // cannot round to more than what we have
            LOGGER.error("Requested division with rounding to {} decimals for target data type {}", this.getClass().getSimpleName());
            throw new ArithmeticException("Requested number of digits for rounding not supported by data type");
        }
        if (mantissa == 0)
            return getZero();           // 0 / x = 0
        if (that.isOne())
            return round(desiredDecimals, rounding); // x / 1 = x
        if (that.isMinusOne())
            return this.negate().round(desiredDecimals, rounding);       // x / -1 = -x

        final int digitsToRound = scale() - desiredDecimals;
        if (digitsToRound <= that.scale()) {
            // we can implement the rounding by reduction of the scale factor, and later multiplication
            return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, that.scale() - digitsToRound, that.mantissa, rounding)
              * POWERS_OF_TEN[digitsToRound]);
        } else {
            // no multiplication at all, rather round
            final long tempMantissa = FixedPointNative.scale_and_divide(mantissa, 0, that.mantissa, rounding) * POWERS_OF_TEN[that.scale()];
            // still needs rounding by digitsToRound - that.scale() digits. FIXME: This is double rounding!
            LOGGER.warn("Double rounding for scales {} / {], desired {}", scale(), that.scale(), desiredDecimals);
            return newInstanceOf(tempMantissa).round(desiredDecimals, rounding);
        }
    }

    /** Divides a fixed point number by an integral one, rounding to decimals digits. The type of the result is the same than that of the left operand. */
    public CLASS divideAndRound(final long that, final int desiredDecimals, final RoundingMode rounding) {
        if (that == 0L) {
            throw new ArithmeticException("Division by 0");
        }
        if (desiredDecimals > scale() || desiredDecimals < 0) {
            // cannot round to more than what we have
            LOGGER.error("Requested division with rounding to {} decimals for target data type {}", this.getClass().getSimpleName());
            throw new ArithmeticException("Requested number of digits for rounding not supported by data type");
        }
        if (mantissa == 0)
            return getZero();           // 0 / x = 0
        if (that == 1L)
            return round(desiredDecimals, rounding); // x / 1 = x

        final int digitsToRound = scale() - desiredDecimals;
        if (digitsToRound <= 0) {
            // we can implement the rounding by reduction of the scale factor, and later multiplication
            return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, -digitsToRound, that, rounding) * POWERS_OF_TEN[digitsToRound]);
        } else {
            // no multiplication at all, rather round
            final long tempMantissa = FixedPointNative.scale_and_divide(mantissa, 0, that, rounding);
            // still needs rounding by digitsToRound - that.scale() digits. FIXME: This is double rounding!
            LOGGER.warn("Double rounding for scales {} / {], desired {}", scale(), 0, desiredDecimals);
            return newInstanceOf(tempMantissa).round(desiredDecimals, rounding);
        }
    }

    /** Divide two fixed point numbers of same type, returning the rounded down integral quotient. */
    public long divideToIntegralValue(final CLASS divisor) {
        return this.mantissa / divisor.mantissa;
    }

    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_divide(final FixedPointBase<?> that) {
        return divide(that, RoundingMode.HALF_EVEN);
    }


    /** Adds two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase<?> gadd(final FixedPointBase<?> that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (mantissa == 0)
            return that;
        if (that.mantissa == 0)
            return this;
        final int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + POWERS_OF_TEN[diff] * that.mantissa);
        else
            return that.newInstanceOf(that.mantissa + POWERS_OF_TEN[-diff] * this.mantissa);
    }

    /** Adds two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS add(final CLASS that) {
        if (that.mantissa == 0L)
            return getMyself();
        if (this.mantissa == 0L)
            return that;
        return this.newInstanceOf(this.mantissa + that.mantissa);
    }

    /** Subtracts two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase<?> gsubtract(final FixedPointBase<?> that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (that.mantissa == 0)
            return this;
        if (mantissa == 0)
            return that.negate();
        final int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - POWERS_OF_TEN[diff] * that.mantissa);
        else
            return that.newInstanceOf(-that.mantissa + POWERS_OF_TEN[-diff] * this.mantissa);
    }
    /** Subtracts two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS subtract(final CLASS that) {
        if (that.mantissa == 0L) {
            return getMyself();
        }
        return this.newInstanceOf(this.mantissa - that.mantissa);
    }

    /** Computes this * a/b, using a specified RoundingMode. */
    public CLASS multAndDivide(final int multiplicator, final int divisor, final RoundingMode roundingMode) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        final long newMantissa = divide_longs(mantissa * multiplicator, divisor, roundingMode);
        return newInstanceOf(newMantissa);
    }

    /** Divides a number by an integer, at maximum precision, using RoundingMode.DOWN. */
    public CLASS divide(final int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1)
            return getMyself();
        if (divisor == -1)
            return this.negate();
        return newInstanceOf(mantissa / divisor);
    }

    /** Xtend syntax sugar. divide maps to the divide method. */
    public CLASS operator_divide(final int divisor) {
        return divide(divisor);
    }

    /** Computes the remainder of a division by an integer. */
    public CLASS remainder(final int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1 || divisor == -1)
            return this.getZero();
        final long quotient = mantissa / divisor;
        return newInstanceOf(mantissa - quotient * divisor);
    }
    /** Xtend syntax sugar. modulo maps to the remainder method. */
    public CLASS operator_modulo(final int divisor) {
        return remainder(divisor);
    }


    /** A scaling and error distribution method with the following properties.
     * Input is an array of numbers, which fulfills the condition that array element 0 is the sum of the others.
     * Desired output is an array with the same condition, plus all values scaled to this currency's scale.
     *
     * The implemented algorithm performs the rounding subject to the following conditions:
     * The resulting difference between the unrounded value and the round value are strictly less than the smallest possible
     * unit in this currency. (This implies that for every index i, there is a rounding strategy ri such that
     *  scaled[i] = unscaled[i].scale(decimals, ri).
     *
     * As an initial strategy, the banker's rounding (aka Gaussian rounding / twopenny rounding) for all elements is performed.
     * If the scaled sum matches, that result is returned.
     * Otherwise, elements are picked for a different rounding strategy in order of increasing relative error.
     *
     * @param unscaledAmounts
     * @return scaled values
     */
    public long[] roundWithErrorDistribution(final long[] unscaledAmounts, final int sourceScale) {
        final int scaleDiff = scale() - sourceScale;
        if (scaleDiff == 0)
            return unscaledAmounts;
        final int n = unscaledAmounts.length;
        final long scaledAmounts[] = new long[n];

        if (scaleDiff > 0) {
            final long factor = FixedPointBase.POWERS_OF_TEN[scaleDiff];
            for (int i = 0; i < n; ++i) {
                scaledAmounts[i] = factor * unscaledAmounts[i];
            }
        } else {
            final long factor = FixedPointBase.POWERS_OF_TEN[-scaleDiff];
            long sum = 0;
            for (int i = 0; i < n; ++i) {
                scaledAmounts[i] = divide_longs(unscaledAmounts[i], factor, RoundingMode.HALF_EVEN);
                if (i > 0)
                    sum += scaledAmounts[i];
            }
            long diff = scaledAmounts[0] - sum; // > 0 : increment scaled amounts
            if (diff != 0) {  // > 0: rounded sum is bigger than sum of elements => increment elements
                // error distribution is required.
                final long adjustment = diff > 0 ? 1 : -1;
                // System.out.println("compareSign=" + compareSign + ", difference="+difference.toPlainString());
                assert (Math.abs(diff) < n);  // can have an error of 1 per item, at most
                final double[] relativeError = new double[n];
                for (int i = 0; i < n; ++i) {
                    // only items are eligible, which have been rounded in the "wrong" way. Namely, only items which have been rounded at all!
                    final long thisDiff = unscaledAmounts[i] - scaledAmounts[i] * factor;
                    // take into account: sign of adjustment, sign of thisDiff, i > 0
                    relativeError[i] = (thisDiff * adjustment * (i > 0 ? 1 : -1) > 0)
                            ? Math.abs(
                                    scaledAmounts[i] == 0
                                        ? (double)unscaledAmounts[i] / (double)factor
                                        : (double)thisDiff / (double)unscaledAmounts[i])
                            : 0.0;
                    // relative error is <= 1 by definition: if unscaled <= 0.5: diff = unscaled, else unscaled > 0.5 and therefore > diff
                    // System.out.println("relative error[" + i + "] = " + relativeError[i]);
                }
                while (diff != 0) {
                    // pick the entry which has the worst error in the current conversion
                    double maxError = 0.0;
                    int pickedIndex = -1;
                    for (int i = 0; i < n; ++i) {
                        if (relativeError[i] > maxError) {
                            maxError = relativeError[i];
                            pickedIndex = i;
                        }
                    }
                    assert (pickedIndex >= 0);             // did actually find one
                    // System.out.println("adjusting index " + pickedIndex);
                    relativeError[pickedIndex] = 0.0;     // mark it "used"
                    if (pickedIndex > 0)
                        scaledAmounts[pickedIndex] += adjustment;
                    else
                        scaledAmounts[pickedIndex] -= adjustment;
                    diff -= adjustment;
                }
            }
        }
        return scaledAmounts;
    }

    @Override
    public int intValue() {
        return (int)(mantissa / getUnitAsLong());
    }

    @Override
    public long longValue() {
        return mantissa / getUnitAsLong();
    }

    @Override
    public float floatValue() {
        return (float)(mantissa / getScaleAsDouble());
    }

    @Override
    public double doubleValue() {
        return mantissa / getScaleAsDouble();
    }

    /** Converts the fixed point number to a BigDecimal which is either ZERO or ONE (with scale 0), or that the same scale as the class of this indicates. */
    public BigDecimal toBigDecimal() {
        if (mantissa == 0) {
            return BigDecimal.ZERO;
        } else if (mantissa == getUnitAsLong()) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.valueOf(mantissa, scale());
        }
    }

    public CLASS scaleByPowerOfTen(final int power) {
        if (power == 0 || mantissa == 0L) {
            return getMyself();
        } else if (power < 0) {
            if (power < -18) {
                return getZero();
            } else {
                return newInstanceOf(mantissa / POWERS_OF_TEN[-power]);
            }
        } else {
            if (power > 18) {
                throw new ArithmeticException("Overflow");
            } else {
                return newInstanceOf(mantissa * POWERS_OF_TEN[power]);
            }
        }
    }

    /** Checks if a given value could be scaled to a different number of digits. */
    public boolean hasMaxScale(final int digits) {
        if (digits < 0) {
            throw new ArithmeticException("Checks for negative max scale not supported");
        }
        if (digits >= scale()) {
            return true;
        }
        final int digitsToScrap = scale() - digits;
        return mantissa % POWERS_OF_TEN[digitsToScrap] == 0L;
    }
}
