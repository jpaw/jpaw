package de.jpaw.fixedpoint;

import java.math.BigDecimal;

public interface FixedPointFactory<CLASS extends FixedPointBase<CLASS>> {
    Class<CLASS> instanceClass();
    CLASS getZero();
    CLASS getOne();
    CLASS of(long mantissa);

    /** creates an instance from the string representation. */
    CLASS valueOf(String s);
    /** creates an instance from an integral value. */
    CLASS valueOf(long n);
    /** creates an instance from some BigDecimal. */
    CLASS valueOf(BigDecimal n);
    /** creates an instance from some double. */
    CLASS valueOf(double n);
}
