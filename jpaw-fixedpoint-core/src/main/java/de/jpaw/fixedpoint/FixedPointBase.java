package de.jpaw.fixedpoint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    private final long mantissa;

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
    public abstract int getScale();

    /** Get the number 0 in the same scale. */
    public abstract CLASS getZero();

    /** Get the number 1 in the same scale. */
    public abstract CLASS getUnit();

    /** Get a reference to myself (essentially "this", but avoids a type cast. */
    public abstract CLASS getMyself();

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
        long scale = powersOfTen[getScale()];
        return mantissa % scale == 0;
    }

    /** Returns the integral part of the number. */
    public long floor() {
        long scale = powersOfTen[getScale()];
        return mantissa / scale;
    }

    public long fraction() {
        long scale = powersOfTen[getScale()];
        long integralDigits = mantissa / scale;
        return Math.abs(mantissa - integralDigits * scale);
    }

    /** Appends a separately provided mantissa in a human readable form to the provided StringBuilder, based on settings of a reference number (this).
     * Method is also used by external classes. */
    public void append(StringBuilder sb, long mantissa) {
        // straightforward implementation discarded due to too much GC overhead (construction of a temporary BigDecimal)
        // return BigDecimal.valueOf(mantissa, getScale()).toPlainString();
        // version with double not considered due to precision loss (mantissa of a double is just 15 digits, we want 18)
        if (getScale() == 0) {
            sb.append(Long.toString(mantissa));
        } else {
            // separate the digits in a way that the fractional ones are not negative
            long scale = powersOfTen[getScale()];
            long integralDigits = mantissa / scale;
            long decimalDigits = Math.abs(mantissa - integralDigits * scale);
            sb.append(integralDigits);
            sb.append('.');
            String decimals = Long.toString(decimalDigits);
            int paddingCharsRequired = getScale() - decimals.length();
            if (paddingCharsRequired > 0) {
                // need padding.
                do {
                    sb.append('0');
                } while (--paddingCharsRequired > 0);
            }
            sb.append(decimals);
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
        if (getScale() == 0) {
            return Long.toString(mantissa);
        } else {
            // we need 21 characters at max (19 digits plus optional sign, plus decimal point), so allocate it with sufficient initial size to avoid realloc
            StringBuilder sb = new StringBuilder(22);
            append(sb, mantissa);
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
        return getScale() + 19 * (int)(mantissa ^ mantissa >>> 32);
    }

    /** As with BigDecimal, equals returns true only of both objects are identical in all aspects. Use compareTo for numerical identity. */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;
        FixedPointBase<?> _that = (FixedPointBase<?>)that;
        return getScale() == _that.getScale() && mantissa == _that.mantissa && this.getClass() == that.getClass();
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
        switch (getScale()) {
        case 18:
            return VariableUnits.valueOf(mantissa / 100, 18);
        case 17:
            return VariableUnits.valueOf(mantissa / 10, 18);
        default:  // 0 .. 16 decimals
            return VariableUnits.valueOf(mantissa, getScale() + 2);
        }
    }

    /** Returns the signum of this number, -1, 0, or +1.
     * Special care is taken in this implementation to work around any kind of integral overflows. */
    @Override
    public int compareTo(FixedPointBase<?> that) {
        // first check is on signum only, to avoid incorrect responses due to integral overflow (MIN_VALUE must be < than MAX_VALUE)
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
        int scaleDiff = this.getScale() - that.getScale();
        if (scaleDiff == 0) {
            // simple: compare the mantissas
            if (this.mantissa == that.mantissa)
                return 0;
            return this.mantissa < that.mantissa ? -1 : 1;
        }
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
        return newInstanceOf(mantissa * factor);
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(int factor) {
        return multiply(factor);
    }

    /** Returns this + 1. */
    public CLASS increment() {
        return newInstanceOf(mantissa + powersOfTen[getScale()]);
    }
    /** Xtend syntax sugar. ++ maps to the increment method. */
    public CLASS operator_plusplus() {
        return increment();
    }

    /** Returns this - 1. */
    public CLASS decrement() {
        return newInstanceOf(mantissa - powersOfTen[getScale()]);
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
        BigDecimal product = BigDecimal.valueOf(this.mantissa, getScale()).multiply(BigDecimal.valueOf(that.mantissa, that.getScale()));
        BigDecimal scaledProduct = product.setScale(targetScale, rounding);
        return scaledProduct.scaleByPowerOfTen(targetScale).unscaledValue().longValue();
    }

    /** Use of native code for scaling and rounding, if required. */
    public long mantissa_of_multiplication(FixedPointBase<?> that, int targetScale, RoundingMode rounding) {
        int digitsToScale = getScale() + that.getScale() - targetScale;
        long mantissaA = this.mantissa;
        long mantissaB = that.mantissa;
        if (digitsToScale <= 0) {
            // easy, no rounding
            return mantissaA * mantissaB * powersOfTen[-digitsToScale];
        }
        return FixedPointNative.multiply_and_scale(mantissaA, mantissaB, digitsToScale, rounding);
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
        return newInstanceOf(mantissa_of_multiplication(that, this.getScale(), rounding));
    }
    /** Multiplies a fixed point number by an another one. The type / scale of the result is the same than that of the left operand. */
    public CLASS multiply(FixedPointBase<?> that, RoundingMode rounding) {
        if (mantissa == 0 || that.mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return getMyself();         // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(mantissa_of_multiplication(that, this.getScale(), rounding));
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public CLASS operator_multiply(FixedPointBase<?> that) {
        return multiply(that, RoundingMode.HALF_EVEN);
    }

    /** Divides a fixed point number by an another one. The type / scale of the result is the same than that of the left operand. */
    public CLASS divide(FixedPointBase<?> that, RoundingMode rounding) {
        if (mantissa == 0)
            return getZero();           // x * 0 = 0 * x = 0
        if (that.isOne())
            return getMyself();         // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return newInstanceOf(FixedPointNative.scale_and_divide(mantissa, that.getScale(), that.mantissa, rounding));
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
        int diff = this.getScale() - that.getScale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(that.mantissa + powersOfTen[-diff] * this.mantissa);
    }
    /** Adds two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS add(CLASS that) {
        int diff = this.getScale() - that.getScale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + powersOfTen[diff] * that.getMantissa());
        else
            return that.newInstanceOf(that.getMantissa() + powersOfTen[-diff] * this.mantissa);
    }

    /** Subtracts two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase<?> gsubtract(FixedPointBase<?> that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (that.mantissa == 0)
            return this;
        if (mantissa == 0)
            return that.negate();
        int diff = this.getScale() - that.getScale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(-that.mantissa + powersOfTen[-diff] * this.mantissa);
    }
    /** Subtracts two fixed point numbers of exactly same type. For variable scale subtypes, the scale of the sum is the bigger of the operand scales. */
    public CLASS subtract(CLASS that) {
        // first checks, if we can void adding the numbers and return either operand.
        int diff = this.getScale() - that.getScale();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - powersOfTen[diff] * that.getMantissa());
        else
            return that.newInstanceOf(-that.getMantissa() + powersOfTen[-diff] * this.mantissa);
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
        int scaleDiff = getScale() - sourceScale;
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
        } else if (mantissa == 1L) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.valueOf(mantissa, getScale());
        }
    }
}
