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

    /** Map to convert rounding mode for negated numbers. */
    private static final EnumMap<RoundingMode,RoundingMode> ROUNDING_MODE_MAPPING = new EnumMap<>(RoundingMode.class);
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
    private final static int [] intPowersOfTen = {  // What's missing here is something like C's "const" for the contents of the array. Let's hope for Java 9, 10 or whatever...
            1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000
    };
    protected final static long [] powersOfTen = {  // What's missing here is something like C's "const" for the contents of the array. Let's hope for Java 9, 10 or whatever...
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

    public final static long getPowerOfTen(int scale) {
        return powersOfTen[scale];
    }

    private transient String asString = null; // due to efforts to return this for arithmetic operations whereever possible, it is likely that the same number will be printed multiple times, and due to Java object alignments, it does not increase the size of the object
    protected final long mantissa;    // the significant digits

    protected FixedPointBase(long mantissa) {
        this.mantissa = mantissa;
    }

    /** Returns a fixed point value object which has the same number of decimals as this, with a given mantissa.
     * This implementation returns cached instances for 0 and 1. Otherwise, in case this has the same mantissa, this is returned. */
    public abstract CLASS newInstanceOf(long mantissa);

    public static long mantissaFor(long integralValue, int scale) {
        return integralValue * powersOfTen[scale];
    }
    public static long mantissaFor(double value, int scale) {
        return Math.round(value * powersOfTen[scale]);
    }
//    /** Returns a fixed point value object which has a different number of decimals. Most implementations have a fixed scale and will not support this. */
//    public CLASS newInstanceOf(long mantissa, int scale) {
//        throw new ArithmeticException("Creating instances of different scale not supported for " + getClass().getCanonicalName());
//    }

    /** Get the number of decimals. */
    public abstract int scale();

    /** Get the number 0 in the same scale. */
    public abstract CLASS getZero();

    /** Get the number 1 in the same scale. */
    public abstract CLASS getUnit();

    /**
     * Get a reference to myself (essentially "this", but avoids a type cast. This is a workaround, required because the compiler currently does not acknowledge that this class is abstract.
     * Invocation is only done from this class, but it must be protected because the derived classed have to override it.
     * */
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
    public boolean isWithinDigits(int numberOfDigits) {
        final long oneMore = powersOfTen[numberOfDigits];
        return -oneMore < mantissa && mantissa < oneMore;
    }

    /** Returns true if to instances of the same subclass will always have the same number of decimals. */
    public boolean isFixedScale() {
        return true;  // default implementations: most subtypes do.
    }

    /** Returns true if this is an integral number. */
    public boolean isIntegralValue() {
        long scale = powersOfTen[scale()];
        return mantissa % scale == 0;
    }

    /** Returns the integral part of the number. */
    public long floor() {
        long scale = powersOfTen[scale()];
        return mantissa / scale;
    }

    public long fraction() {
        long scale = powersOfTen[scale()];
        long integralDigits = mantissa / scale;
        return Math.abs(mantissa - integralDigits * scale);
    }

    // only called for digits != 0 && scale > 0
    private static void appendFraction(StringBuilder sb, int scale, int digits) {
        do {
            int nextPower = intPowersOfTen[--scale];
            int nextDigit = digits / nextPower;
            sb.append(DIGITS[nextDigit]);
            digits -= nextDigit * nextPower;
        } while (digits != 0);  // replace condition by 'scale > 0' to get full length of fractional digits
    }
    private static void appendFraction(StringBuilder sb, int scale, long digits) {
        do {
            long nextPower = powersOfTen[--scale];
            long nextDigit = digits / nextPower;
            sb.append(DIGITS[(int) nextDigit]);
            digits -= nextDigit * nextPower;
        } while (digits != 0L);  // replace condition by 'scale > 0' to get full length of fractional digits
    }

    /** Appends a separately provided mantissa in a human readable form to the provided StringBuilder, based on settings of a reference number (this).
     * Method is also used by external classes. */
    public static void append(StringBuilder sb, long mantissa, int scale) {
        // straightforward implementation discarded due to too much GC overhead (construction of a temporary BigDecimal)
        // return BigDecimal.valueOf(mantissa, scale()).toPlainString();
        // version with double not considered due to precision loss (mantissa of a double is just 15 digits, we want 18)
        if (scale == 0) {
            sb.append(mantissa);
        } else {
            // separate the digits in a way that the fractional ones are not negative
            long ten2scale = powersOfTen[scale];
            long integralDigits = mantissa / ten2scale;
            long decimalDigits = Math.abs(mantissa - integralDigits * ten2scale);
            sb.append(integralDigits);
            // conditional append of fractional part
            if (decimalDigits != 0L) {
                sb.append('.');
                if (decimalDigits <= 999_999_999) {
                    // max 9 digits: do it with integers, to avoid costly 6 bit divisions
                    appendFraction(sb, scale, (int)decimalDigits);
                } else {
                    appendFraction(sb, scale, decimalDigits);
                }
            }
        }
    }

    /** Returns the value in a human readable form. The same notes as for BigDecimal.toString() apply:
     * <ul>
     * <li>There is a one-to-one mapping between the distinguishable VariableUnits values and the result of this conversion. That is, every distinguishable VariableUnits value (unscaled value and scale) has a unique string representation as a result of using toString. If that string representation is converted back to a VariableUnits using the VariableUnits(String) constructor, then the original value will be recovered.</li>
     * <li>The string produced for a given number is always the same; it is not affected by locale. This means that it can be used as a canonical string representation for exchanging decimal data, or as a key for a Hashtable, etc. Locale-sensitive number formatting and parsing is handled by the NumberFormat class and its subclasses.</li>
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
                StringBuilder sb = new StringBuilder(22);
                append(sb, mantissa, scale());
                asString = sb.toString();
            }
        }
        return asString;
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
    static protected final long parseMantissa(String src, int targetScale) {
        int indexOfDecimalPoint = src.indexOf('.');
        if (indexOfDecimalPoint < 0) {
            // no point included, easy case, integral number
            return Long.parseLong(src) * powersOfTen[targetScale];
        } else {
            // parse the integral part, then the fractional part. Support special case to capture -.4 or +.4 (Long.parse("-") won't work!)
            long integralPart = (
                    indexOfDecimalPoint == 1 && (src.charAt(0) == '-' || src.charAt(0) == '+'))
                    ? 0
                    : Long.parseLong(src.substring(0, indexOfDecimalPoint)) * powersOfTen[targetScale];
            int decimalDigitsFound = src.length() - indexOfDecimalPoint - 1;
            if (decimalDigitsFound == 0) {   // the "1." case => same as integral case
                return integralPart;
            }
            String fraction = src.substring(indexOfDecimalPoint + 1);
            if (fraction.charAt(0) == '-' || fraction.charAt(0) == '+')
                throw new NumberFormatException("Extra sign found at start of fractional digits");
            long fractionalPart = Long.parseLong(fraction);  // parseUnsignedLong still allows a leading plus sign
            // apply the sign to the fractional part. checking integralPart won't work here, because that may be "-0"
            if (src.charAt(0) == '-')
                fractionalPart = -fractionalPart;
            int fractionalDigitsDiff = targetScale - fraction.length();
            if (fractionalDigitsDiff >= 0) {
                // no rounding required
                return integralPart + fractionalPart * powersOfTen[fractionalDigitsDiff];
            } else {
                // if we have too may significant digits, throw an Exception
                if (fractionalPart % powersOfTen[-fractionalDigitsDiff] != 0)
                    throw new NumberFormatException("Too many significant fractional digits specified: " + src + ", allowed: " + targetScale);
                return integralPart + fractionalPart / powersOfTen[-fractionalDigitsDiff];
            }
        }
    }

    static public final long mantissaFor(String src, int targetScale) {
        return parseMantissa(src, targetScale);
    }

    static public final long mantissaFor(long currentMantissa, int currentScale, int desiredScale, boolean allowRounding) {
        if (currentMantissa == 0L) {
            return currentMantissa;
        }
        final int toMultiplyWithExponent = desiredScale - currentScale;
        if (toMultiplyWithExponent >= 0) {
            if (toMultiplyWithExponent > 18) {
                throw new ArithmeticException("Overflow");
            }
            return currentMantissa * powersOfTen[toMultiplyWithExponent];
        } else {
            if (!allowRounding) {
                if (toMultiplyWithExponent < -18 || currentMantissa % powersOfTen[toMultiplyWithExponent] != 0L) {
                    throw new ArithmeticException("Rounding required but not allowed");
                }
            }
            if (toMultiplyWithExponent < -18) {
                return 0L; // underflow
            }
            return currentMantissa / powersOfTen[toMultiplyWithExponent];
        }
    }

    @Override
    public int hashCode() {
        return scale() + 19 * (int)(mantissa ^ mantissa >>> 32);
    }

    /** As with BigDecimal, equals returns true only of both objects are identical in all aspects. Use compareTo for numerical identity. */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;
        FixedPointBase<?> _that = (FixedPointBase<?>)that;
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
    public int compareTo(FixedPointBase<?> that) {
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
            long diff = mantissa - that.mantissa / powersOfTen[-scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa * powersOfTen[-scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        } else {
            long diff = mantissa  / powersOfTen[scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa - that.mantissa * powersOfTen[scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        }
    }
    /** Xtend syntax sugar. spaceship maps to the compareTo method. */
    public int operator_spaceship(FixedPointBase<?> that) {
        return compareTo(that);
    }
    public boolean operator_equals(FixedPointBase<?> that) {
        return compareTo(that) == 0;
    }
    public boolean operator_notEquals(FixedPointBase<?> that) {
        return compareTo(that) != 0;
    }
    public boolean operator_lessThan(FixedPointBase<?> that) {
        return compareTo(that) < 0;
    }
    public boolean operator_lessEquals(FixedPointBase<?> that) {
        return compareTo(that) <= 0;
    }
    public boolean operator_greaterThan(FixedPointBase<?> that) {
        return compareTo(that) > 0;
    }
    public boolean operator_greaterEquals(FixedPointBase<?> that) {
        return compareTo(that) >= 0;
    }

    /** Returns the smaller of this and the parameter. */
    public CLASS min(CLASS that) {
        return this.compareTo(that) <= 0 ? getMyself() : that;
    }

    /** Returns the bigger of this and the parameter. */
    public CLASS max(CLASS that) {
        return this.compareTo(that) >= 0 ? getMyself() : that;
    }

    /** Returns the smaller of this and the parameter, allows different type parameters. */
    public FixedPointBase<?> gmin(FixedPointBase<?> that) {
        return this.compareTo(that) <= 0 ? this : that;
    }

    /** Returns the bigger of this and the parameter, allows different type parameters. */
    public FixedPointBase<?> gmax(FixedPointBase<?> that) {
        return this.compareTo(that) >= 0 ? this : that;
    }

    /** Multiplies a fixed point number by an integral factor. The scale (and type) of the product is the same as the one of this. */
    public CLASS multiply(int factor) {
        return newInstanceOf(mantissa * factor);  // newInstanceOf optimizes the cases of factors 0 and 1
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(int factor) {
        return multiply(factor);
    }

    /** Returns this + 1. */
    public CLASS increment() {
        return newInstanceOf(mantissa + powersOfTen[scale()]);
    }
    /** Xtend syntax sugar. ++ maps to the increment method. */
    public CLASS operator_plusplus() {
        return increment();
    }

    /** Returns this - 1. */
    public CLASS decrement() {
        return newInstanceOf(mantissa - powersOfTen[scale()]);
    }
    /** Xtend syntax sugar. -- maps to the decrement method. */
    public CLASS operator_minusminus() {
        return decrement();
    }

    /** Subroutine to provide the mantissa of a multiplication. */
    /*
    public long mantissa_of_multiplication(FixedPointBase<?> that, int targetScale, RoundingMode rounding) {
        int digitsToScale = getDecimals() + that.getDecimals() - targetScale;
        long mantissaA = this.mantissa;
        long mantissaB = that.mantissa;
        if (digitsToScale <= 0) {
            // easy, no rounding
            return mantissaA * mantissaB * powersOfTen[-digitsToScale];
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
            if (unroundedProduct % powersOfTen[digitsToScale] != 0L)
                throw new ArithmeticException("Rounding required but forbidden, scaling " + unroundedProduct + " by " + digitsToScale + " digits");
            return sign * unroundedProduct % powersOfTen[digitsToScale];
        case DOWN:
            return sign * unroundedProduct / powersOfTen[digitsToScale];
        case UP:
            work = unroundedProduct % powersOfTen[digitsToScale];
            return sign * (unroundedProduct / powersOfTen[digitsToScale] + (work != 0 ? 1 : 0));
        case HALF_UP:
            work = unroundedProduct % powersOfTen[digitsToScale];
            return sign * (unroundedProduct / powersOfTen[digitsToScale] + (work >= (powersOfTen[digitsToScale] >> 1) ? 1 : 0));
        case HALF_DOWN:
            work = unroundedProduct % powersOfTen[digitsToScale];
            return sign * (unroundedProduct / powersOfTen[digitsToScale] + (work > (powersOfTen[digitsToScale] >> 1) ? 1 : 0));
        case CEILING:
            work = unroundedProduct % powersOfTen[digitsToScale];
            return sign * unroundedProduct / powersOfTen[digitsToScale] + (work != 0 ? 1 : 0);
        case FLOOR:
            work = unroundedProduct % powersOfTen[digitsToScale];
            return sign * unroundedProduct / powersOfTen[digitsToScale] - (sign < 0 && work != 0 ? 1 : 0);
        case HALF_EVEN:
            work = unroundedProduct % (powersOfTen[digitsToScale] << 1);
            // round as follows: [0, 0.5] down, (0.5, 1) up, [1, 1.5) down, [1.5, 2) up
            return sign * (unroundedProduct / powersOfTen[digitsToScale] + (work >= (powersOfTen[digitsToScale] >> 1) ? 1 : 0));
        default:
            return 0;   // FIXME
        }
    }
    */

    /** Short source, but high GC overhead version, as a testing reference. */
    public long mantissa_of_multiplication_using_BD(FixedPointBase<?> that, int targetScale, RoundingMode rounding) {
        BigDecimal product = BigDecimal.valueOf(this.mantissa, scale()).multiply(BigDecimal.valueOf(that.mantissa, that.scale()));
        BigDecimal scaledProduct = product.setScale(targetScale, rounding);
        return scaledProduct.scaleByPowerOfTen(targetScale).unscaledValue().longValue();
    }

    /**
     * Use of native code for scaling and rounding, if required.
     * Private method, currently exposed for benchmarking purposes (to avoid effect of GC).
     * */
    public long mantissa_of_multiplication(FixedPointBase<?> that, int targetScale, RoundingMode rounding) {
        int digitsToScale = scale() + that.scale() - targetScale;
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
            productAbsolute = mantissaA * mantissaB * powersOfTen[-digitsToScale];
        } else {
            // invoke the computation. If we have changed the sign, adjust the requested rounding mode accordingly.
            // check if we can do it with a long
            if (((mantissaA | mantissaB) & 0xffffffff80000000L) == 0L) {
                // both have 31 bits only
                productAbsolute = roundMantissa(mantissaA * mantissaB, powersOfTen[digitsToScale], negateResult ? ROUNDING_MODE_MAPPING.get(rounding) : rounding);
            } else {
                productAbsolute = FixedPointNative.multiply_and_scale(mantissaA, mantissaB, digitsToScale, negateResult ? ROUNDING_MODE_MAPPING.get(rounding) : rounding);
            }
        }
        return negateResult ? -productAbsolute : productAbsolute;
    }

    private static long roundMantissa(long in, long powerOfTen, RoundingMode roundingMode) {
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
            long dec = 2 * remainder - powerOfTen;
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

    public CLASS round(int desiredScale, RoundingMode rounding) {
        int power = scale() - desiredScale;
        if (power <= 0 || mantissa == 0L) {
            // already by design
            return getMyself();
        }
        final long div = powersOfTen[power];
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
    public static long divide_longs(long a, long b, RoundingMode rounding) {
        long tmp = a / b;
        long mod = a % b;
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
    public FixedPointBase<?> gmultiply(FixedPointBase<?> that, RoundingMode rounding) {
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
    public CLASS multiply(FixedPointBase<?> that, RoundingMode rounding) {
        if (mantissa == 0 || that.mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return getMyself();         // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(mantissa_of_multiplication(that, this.scale(), rounding));
    }

    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(FixedPointBase<?> that) {
        return multiply(that, RoundingMode.HALF_EVEN);
    }

    /**
     * Multiplies a fixed point number by an another one. The type of the result is the same than that of the left operand.
     * The scale of the result is also the same as of the left operand, but the result is rounded to fewer digits.
     * This must be performed directly after multiplication, because a two step rounding could return different results:
     * 0.445 => round(2) = 0.45, => round(1) = 0.5, while 0.445 => round(1) = 0.4 */
    public CLASS multiplyAndRound(FixedPointBase<?> that, int desiredDecimals, RoundingMode rounding) {
        if (desiredDecimals > scale() || desiredDecimals < 0) {
            // cannot round to more than what we have
            LOGGER.error("Requested rounding to {} decimals for target data type {}", this.getClass().getSimpleName());
            throw new ArithmeticException("Requested number of digits for rounding not supported by data type");
        }
        if (mantissa == 0 || that.mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        // no tests for second operand equal to one here, due to possible rounding effects
        return newInstanceOf(mantissa_of_multiplication(that, desiredDecimals, rounding) * powersOfTen[scale() - desiredDecimals]);
    }



    /** Divides a fixed point number by an another one. The type / scale of the result is the same than that of the left operand. */
    public CLASS divide(FixedPointBase<?> that, RoundingMode rounding) {
        if (mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return getMyself();         // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, that.scale(), that.mantissa, rounding));
    }

    /** Divides a fixed point number by an another one, rounding to decimals digits. The type of the result is the same than that of the left operand. */
    public CLASS divideAndRound(FixedPointBase<?> that, int desiredDecimals, RoundingMode rounding) {
        if (desiredDecimals > scale() || desiredDecimals < 0) {
            // cannot round to more than what we have
            LOGGER.error("Requested division with rounding to {} decimals for target data type {}", this.getClass().getSimpleName());
            throw new ArithmeticException("Requested number of digits for rounding not supported by data type");
        }
        if (mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return round(desiredDecimals, rounding); // x * 1 = x
        if (that.isMinusOne())
            return this.negate().round(desiredDecimals, rounding);       // x * -1 = -x

        int digitsToRound = scale() - desiredDecimals;
        if (digitsToRound <= that.scale()) {
            // we can implement the rounding by reduction of the scale factor, and later multiplication
            return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, that.scale() - digitsToRound, that.mantissa, rounding) * powersOfTen[digitsToRound]);
        } else {
            // no multiplication at all, rather round
            final long tempMantissa = FixedPointNative.scale_and_divide(mantissa, 0, that.mantissa, rounding) * powersOfTen[that.scale()];
            // still needs rounding by digitsToRound - that.scale() digits. FIXME: This is double rounding!
            LOGGER.warn("Double rounding for scales {} / {], desired {}", scale(), that.scale(), desiredDecimals);
            return newInstanceOf(tempMantissa).round(desiredDecimals, rounding);
        }
    }

    /** Divide two fixed point numbers of same type, returning the rounded down integral quotient. */
    public long divideToIntegralValue(CLASS divisor) {
        return this.mantissa / divisor.mantissa;
    }

    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_divide(FixedPointBase<?> that) {
        return divide(that, RoundingMode.HALF_EVEN);
    }


    /** Adds two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase<?> gadd(FixedPointBase<?> that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (mantissa == 0)
            return that;
        if (that.mantissa == 0)
            return this;
        int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(that.mantissa + powersOfTen[-diff] * this.mantissa);
    }

    /** Adds two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS add(CLASS that) {
        if (that.mantissa == 0L)
            return getMyself();
        if (this.mantissa == 0L)
            return that;
        return this.newInstanceOf(this.mantissa + that.mantissa);
    }

    /** Subtracts two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase<?> gsubtract(FixedPointBase<?> that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (that.mantissa == 0)
            return this;
        if (mantissa == 0)
            return that.negate();
        int diff = this.scale() - that.scale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(-that.mantissa + powersOfTen[-diff] * this.mantissa);
    }
    /** Subtracts two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS subtract(CLASS that) {
        if (that.mantissa == 0L) {
            return getMyself();
        }
        return this.newInstanceOf(this.mantissa - that.mantissa);
    }

    /** Divides a number by an integer. */
    public CLASS divide(int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1)
            return getMyself();
        if (divisor == -1)
            return this.negate();
        return newInstanceOf(mantissa / divisor);
    }
    /** Xtend syntax sugar. divide maps to the divide method. */
    public CLASS operator_divide(int divisor) {
        return divide(divisor);
    }

    /** Computes the remainder of a division by an integer. */
    public CLASS remainder(int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1 || divisor == -1)
            return this.getZero();
        long quotient = mantissa / divisor;
        return newInstanceOf(mantissa - quotient * divisor);
    }
    /** Xtend syntax sugar. modulo maps to the remainder method. */
    public CLASS operator_modulo(int divisor) {
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
    public long [] roundWithErrorDistribution(long [] unscaledAmounts, int sourceScale) {
        int scaleDiff = scale() - sourceScale;
        if (scaleDiff == 0)
            return unscaledAmounts;
        int n = unscaledAmounts.length;
        long scaledAmounts [] = new long [n];

        if (scaleDiff > 0) {
            final long factor = FixedPointBase.powersOfTen[scaleDiff];
            for (int i = 0; i < n; ++i)
                scaledAmounts[i] = factor * unscaledAmounts[i];
        } else {
            final long factor = FixedPointBase.powersOfTen[-scaleDiff];
            long sum = 0;
            for (int i = 0; i < n; ++i) {
                scaledAmounts[i] = divide_longs(unscaledAmounts[i], factor, RoundingMode.HALF_EVEN);
                if (i > 0)
                    sum += scaledAmounts[i];
            }
            long diff = scaledAmounts[0] - sum; // > 0 : increment scaled amounts
            if (diff != 0) {  // > 0: rounded sum is bigger than sum of elements => increment elements
                // error distribution is required.
                long adjustment = diff > 0 ? 1 : -1;
                // System.out.println("compareSign=" + compareSign + ", difference="+difference.toPlainString());
                assert(Math.abs(diff) < n);  // can have an error of 1 per item, at most
                double [] relativeError = new double [n];
                for (int i = 0; i < n; ++i) {
                    // only items are eligible, which have been rounded in the "wrong" way. Namely, only items which have been rounded at all!
                    long thisDiff = unscaledAmounts[i] - scaledAmounts[i] * factor;
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
                    assert(pickedIndex >= 0);             // did actually find one
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
        return (float)((double)mantissa / getScaleAsDouble());
    }

    @Override
    public double doubleValue() {
        return (double)mantissa / getScaleAsDouble();
    }

    public BigDecimal toBigDecimal() {
        if (mantissa == 0) {
            return BigDecimal.ZERO;
        } else if (mantissa == getUnit().mantissa) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.valueOf(mantissa, scale());
        }
    }

    public CLASS scaleByPowerOfTen(int power) {
        if (power == 0 || mantissa == 0L) {
            return getMyself();
        } else if (power < 0) {
            if (power < -18) {
                return getZero();
            } else {
                return newInstanceOf(mantissa / powersOfTen[power]);
            }
        } else {
            if (power > 18) {
                throw new ArithmeticException("Overflow");
            } else {
                return newInstanceOf(mantissa * powersOfTen[power]);
            }
        }
    }

    /** Checks if a given value could be scaled to a different number of digits. */
    public boolean hasMaxScale(int digits) {
        if (digits < 0) {
            throw new ArithmeticException("Checks for negative max scale not supported");
        }
        if (digits >= scale()) {
            return true;
        }
        int digitsToScrap = scale() - digits;
        return mantissa % powersOfTen[digitsToScrap] == 0L;
    }
}
