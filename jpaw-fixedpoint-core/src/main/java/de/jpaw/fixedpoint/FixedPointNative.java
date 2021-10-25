package de.jpaw.fixedpoint;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Support class which performs 64 * 64 bit to 128 bit multiplication, and subsequent scaling. */
public final class FixedPointNative {
    private static final String LIBRARY_NAME = "jpawFP128";
    private static volatile boolean nativeAvailable = false;
    static {
        try {
            System.loadLibrary(LIBRARY_NAME); // Load native library at runtime
            System.out.println("Successfully loaded native library " + LIBRARY_NAME);
            nativeAvailable = true;
        } catch (final Exception e) {
            System.out.println("Failed to load native library " + LIBRARY_NAME + ": " + e);
        } catch (final UnsatisfiedLinkError e) {
            System.out.println("Failed to call native library " + LIBRARY_NAME + ": " + e);
        }
    }

    private FixedPointNative() { }

    /** Computes a * b / c, with a 128 bit intermediate result. c is known to be a strictly positive number. */
    private static native long multdiv128(long a, long b, long c, int roundingMode);

    /** multiply and divide - generic entry. Computes a * p / q. */
    public static long mult_div(final long a, final long p, final long q, final RoundingMode rounding) {
        if (nativeAvailable) {
            return multdiv128(a, p, q, rounding.ordinal());
        }
        // fallback implementation.
        final BigDecimal product = BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(p)).divide(BigDecimal.valueOf(q), rounding);
        return product.longValue();
    }


    /** decimalsScale is in range 0..18 */
    public static long multiply_and_scale(final long mantissaA, final long mantissaB, final int decimalsScale, final RoundingMode rounding) {
        if (nativeAvailable) {
            return multdiv128(mantissaA, mantissaB, FixedPointBase.POWERS_OF_TEN[decimalsScale], rounding.ordinal());
        }
        // fallback implementation. Is there a better one with just BigInteger?
        final BigDecimal product = BigDecimal.valueOf(mantissaA).multiply(BigDecimal.valueOf(mantissaB, decimalsScale));
        final BigDecimal scaledProduct = product.setScale(0, rounding);
        return scaledProduct.longValue();
    }

    /** decimalsScale is in range 0..18 */
    public static long scale_and_divide(long mantissa, final int decimalsScale, long divisor, final RoundingMode rounding) {
        if (divisor < 0) {
            // move the sign to the dividend
            divisor  = -divisor;
            mantissa = -mantissa;
        }
        if (nativeAvailable) {
            return multdiv128(mantissa, FixedPointBase.POWERS_OF_TEN[decimalsScale], divisor, rounding.ordinal());
        }
        // fallback implementation. Is there a better one with just BigInteger?
        final BigDecimal quotient = BigDecimal.valueOf(mantissa).divide(BigDecimal.valueOf(divisor, decimalsScale), 0, rounding);
        return quotient.longValue();
    }

}
