package de.jpaw.fixedpoint.money;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.FixedPointNative;
import de.jpaw.fixedpoint.types.VariableUnits;

/** A currency with at least one amount field (gross) and an optional breakdown (for example into net + taxes).
 * Instances of this class are immutable. */
public class FPAmount implements Serializable {
    private static final long serialVersionUID = -626929116120293201L;

    private static long [] EMPTY_ARRAY = new long [0];
    private static List<Long> EMPTY_LIST = ImmutableList.<Long>of();
    private final FPCurrency currency;
    private final long gross;
    private final long [] amounts;      // partial amounts - net + taxes, at least 1 elements if the array exists


    // some getters...
    public FPCurrency getCurrency() {
        return currency;
    }
    public long getGross() {
        return gross;
    }
    public List<Long> getAmounts() {
        // array must be mapped to a list, as the array is not immutable
        if (amounts.length == 0)
            return EMPTY_LIST;
        List<Long> result = new ArrayList<Long>(amounts.length);
        for (int i = 0; i < amounts.length; ++i)
            result.add(amounts[i]);
        return result;
    }


    private final void validate(long gross, long [] amounts) {
        for (int i = 0; i < amounts.length; ++i)
            gross -= amounts[i];
        if (gross != 0)
            throw new IllegalArgumentException("gross does not match sum of amounts");
    }

    /** External validation method. Mainly for testing. It should never fail.
     * Does only something is there is some breakdown into amounts. */
    public void validate() {
        if (amounts.length > 0)
            validate(gross, amounts);
    }

    /** Creates a single amount from a Currency and a fixed point number. */
    public FPAmount(FixedPointBase<?> amount, CurrencyData currency) {
        this.currency = new FPCurrency(currency, amount);
        this.gross = amount.getMantissa();
        this.amounts = EMPTY_ARRAY;
    }

    public FPAmount(List<Long> amounts, long gross, FPCurrency currency) {
        this.currency = currency;
        this.gross = gross;
        if (amounts != null && amounts.size() > 0) {
            // convert the list to an array
            long [] netAndTaxes = new long [amounts.size()];
            for (int i = 0; i < amounts.size(); ++i)
                netAndTaxes[i] = amounts.get(i);
            // if there is a breakdown of amounts, then the sum of the components must match that
            validate(gross, netAndTaxes);
            this.amounts = netAndTaxes;
        } else {
            this.amounts = EMPTY_ARRAY;
        }
    }
    public FPAmount(FPCurrency currency, long gross, long ... amounts) {
        this.currency = currency;
        this.gross = gross;
        if (amounts != null && amounts.length > 0) {
            // if there is a breakdown of amounts, then the sum of the components must match that
            validate(gross, amounts);
            this.amounts = Arrays.copyOf(amounts, amounts.length);
        } else {
            this.amounts = EMPTY_ARRAY;
        }
    }

    /** Private constructor, which is essentially the same as the previous one, but avoids the array copy.
     * To be used for cases when the passed array is known to be constructed solely for the new object anyway.
     * @param gross
     * @param amounts
     * @param currency
     */
    private FPAmount(long gross, long [] amounts, FPCurrency currency) {
        this.currency = currency;
        this.gross = gross;
        this.amounts = amounts;
    }


    /** Prints the fixed point amount in form "gross currency [list of net / taxes]", where the list of components is optional. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        currency.getZero().append(sb, gross);       // prints the gross amount formatted as required by the currency
        sb.append(' ');
        sb.append(currency);
        if (amounts.length > 0) {
            for (int i = 0; i < amounts.length; ++i) {
                if (i == 0) {
                    sb.append(' ');
                    sb.append('[');
                } else {
                    sb.append(',');
                    sb.append(' ');
                }
                currency.getZero().append(sb, amounts[i]);
            }
            sb.append(']');
        }
        return sb.toString();
    }

    private void checkMatchingType(FPAmount that) {
        if (!currency.equals(that.currency))
            throw new IllegalArgumentException("Currencies don't match");
        if (amounts.length != that.amounts.length)
            throw new IllegalArgumentException("Number of components don't match (this=" + amounts.length + ", that=" + that.amounts.length + ")");
    }

    /** Returns zero for the same currency, precision and number of taxes. */
    public FPAmount zero() {
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i)
                sum[i] = 0;
        }
        return new FPAmount(0, sum, currency);
    }
    /** Returns the negative of this amount for the same currency, precision and number of taxes. */
    public FPAmount negate() {
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i)
                sum[i] = -amounts[i];
        }
        return new FPAmount(-gross, sum, currency);
    }
    public FPAmount add(FPAmount that) {
        checkMatchingType(that);
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i)
                sum[i] = amounts[i] + that.amounts[i];
        }
        return new FPAmount(gross + that.gross, sum, currency);
    }
    public FPAmount subtract(FPAmount that) {
        checkMatchingType(that);
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i)
                sum[i] = amounts[i] - that.amounts[i];
        }
        return new FPAmount(gross - that.gross, sum, currency);
    }
    public FPAmount multiply(int factor) {
        if (factor == 1)
            return this;        // nothing to do
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i)
                sum[i] = amounts[i] * factor;
        }
        return new FPAmount(gross * factor, sum, currency);
    }
    /** multiply the number by a rational p/q, roundign the result, if required. */
    public FPAmount multiply(long p, long q, RoundingMode rounding) {
        if (q == 0)
            throw new ArithmeticException("Division by 0");
        if (p == q)
            return this;        // nothing to do
        long scaledGross = FixedPointNative.mult_div(gross, p, q, rounding);
        long [] sum = EMPTY_ARRAY;
        if (amounts.length > 0) {
            long sumComponents = 0;
            sum = new long [amounts.length];
            for (int i = 0; i < amounts.length; ++i) {
                sum[i] = FixedPointNative.mult_div(amounts[i], p, q, rounding);
                sumComponents += sum[i];
            }
            // perform error distribution, if required
            if (sumComponents != scaledGross) {
                // TODO: rounding distribution FIXME
                scaledGross = sumComponents;
            }
        }
        return new FPAmount(scaledGross, sum, currency);
    }

    /** Add tax percentages to a scalar amount. */
    public FPAmount netToGross(List<? extends FixedPointBase<?>> taxes) {
        if (amounts.length > 0)
            throw new ArithmeticException("Cannot add tax to an amount with already existing tax values");
        if (taxes.size() == 0)
            return this;  // no taxes to be added

        // allocate a list for net and all tax amounts
        long [] netAndTaxes = new long [taxes.size() + 1];
        long newGross = gross;
        netAndTaxes[0] = gross;
        int i = 0;
        for (FixedPointBase<?> t : taxes) {
            long ta = FixedPointNative.multiply_and_scale(gross, t.getMantissa(), t.getScale(), RoundingMode.HALF_EVEN);
            newGross += ta;
            netAndTaxes[++i] = ta;
        }
        return new FPAmount(newGross, netAndTaxes, currency);
    }

    /** Subtract tax percentages from a scalar amount. */
    public FPAmount grossToNet(List<? extends FixedPointBase<?>> taxes) {
        if (amounts.length > 0)
            throw new ArithmeticException("Cannot subtract tax from an amount with already existing tax values");
        if (taxes.size() == 0)
            return this;  // no taxes to be subtracted

        // compute the total tax factor
        VariableUnits totalTaxPercentage = VariableUnits.sumOf(taxes, true);
        long totalMantissa = totalTaxPercentage.getMantissa();
        int totalScale = totalTaxPercentage.getScale();
        // allocate a list for net and all tax amounts
        long [] netAndTaxes = new long [taxes.size() + 1];
        netAndTaxes[0] = FixedPointNative.scale_and_divide(gross, totalScale, totalMantissa, RoundingMode.HALF_EVEN);
        long newGross = netAndTaxes[0];
        int i = 0;
        for (FixedPointBase<?> t : taxes) {
            // ta = gross * t / (1+total)   we calc both in scale of total
            long mantissaT = t.getMantissa() * FixedPointBase.getPowerOfTen(totalScale - t.getScale());
            long ta = FixedPointNative.mult_div(gross, mantissaT, totalMantissa, RoundingMode.HALF_EVEN);
            newGross += ta;
            netAndTaxes[++i] = ta;
        }
        // perform error distribution, if required
        if (newGross != gross) {
            // TODO: rounding distribution FIXME
            netAndTaxes[0] -= newGross - gross;
        }

        return new FPAmount(gross, netAndTaxes, currency);
    }

    /** Multiply an Amount by a scalar factor. Return the result in the same precision as the left operand. */
    public FPAmount multiply(FixedPointBase<?> factor) {
        if (factor.isOne())
            return this;
        if (factor.isMinusOne())
            return this.negate();
        if (factor.isZero())
            return this.zero();
        // work...
        int factorScale = factor.getScale();
        long factorMantissa = factor.getMantissa();
        long [] sum = EMPTY_ARRAY;
        long newGross = factorScale == 0 ? (gross * factorMantissa)
                : FixedPointNative.multiply_and_scale(gross, factorMantissa, factorScale, RoundingMode.HALF_EVEN);
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            if (factorScale == 0) {
                for (int i = 0; i < amounts.length; ++i)
                    sum[i] = amounts[i] * factorMantissa;
            } else {
                for (int i = 0; i < amounts.length; ++i)
                    sum[i] = FixedPointNative.multiply_and_scale(amounts[i], factorMantissa, factorScale, RoundingMode.HALF_EVEN);
                // TODO: difference correction
                // FIXME: below code is not optimal, need error distribution
                newGross = 0;
                for (int i = 0; i < amounts.length; ++i)
                    newGross += sum[i];
            }
        }
        return new FPAmount(newGross, sum, currency);
    }

    /** Multiply an Amount by a scalar factor and convert it to a new currency. */
    public FPAmount convert(FixedPointBase<?> factor, FPCurrency newCurrency) {
        // no shortcuts here...
        int factorScale = factor.getScale() - newCurrency.getDecimals() + currency.getDecimals();
        long factorMantissa = factor.getMantissa();
        long [] sum = EMPTY_ARRAY;
        long newGross = factorScale <= 0 ? (gross * factorMantissa * FixedPointBase.getPowerOfTen(-factorScale))
                : FixedPointNative.multiply_and_scale(gross, factorMantissa, factorScale, RoundingMode.HALF_EVEN);
        if (amounts.length > 0) {
            sum = new long [amounts.length];
            if (factorScale <= 0) {
                for (int i = 0; i < amounts.length; ++i)
                    sum[i] = amounts[i] * factorMantissa * FixedPointBase.getPowerOfTen(-factorScale);
            } else {
                for (int i = 0; i < amounts.length; ++i)
                    sum[i] = FixedPointNative.multiply_and_scale(amounts[i], factorMantissa, factorScale, RoundingMode.HALF_EVEN);
                // TODO: difference correction
                // FIXME: below code is not optimal, need error distribution
                newGross = 0;
                for (int i = 0; i < amounts.length; ++i)
                    newGross += sum[i];
            }
        }
        return new FPAmount(newGross, sum, newCurrency);
    }

    @Override
    public int hashCode() {
        int hash = (int)(gross ^ (gross >>> 32)) + 31 * currency.hashCode();     // for Java 1.8ff, also Long.hashCode(gross) works!
        if (amounts != null)
            hash = 31 * hash + Arrays.hashCode(amounts);
        return hash;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null ||getClass() != that.getClass())
            return false;
        FPAmount _that = (FPAmount)that;
        return _that.currency.equals(currency) && _that.gross == gross && Arrays.equals(amounts, _that.amounts);
    }
}
