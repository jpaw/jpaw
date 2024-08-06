package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.fixedpoint.types.MicroUnits;


public class TestRatios {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRatios.class);

    private static final MicroUnits START_VALUE = MicroUnits.of(15_777_333_234_567_321L);  // need something which definitely exceeds 32 bits
    private static final long EPSILON = 1L;  // we cannot compare to 1, because we have varying rounding modes

    @Test
    public void testRatioSimple() throws Exception {
        // simple test for debugging / single stepping
        final MicroUnits result = START_VALUE.multAndDivide(777_666_333, 999_888_444, RoundingMode.HALF_EVEN);
        LOGGER.debug("result is {}", result);
    }

    protected void testRatios(final MicroUnits startNumber, final int startNominator, final int startDenominator,
      final int scalePerIteration, final RoundingMode roundingMode) {
        final MicroUnits compareResult = startNumber.multAndDivide(startNominator, startDenominator, roundingMode);

        int nominator = startNominator;
        int denominator = startDenominator;
        for (;;) {
            long n = (long)nominator * scalePerIteration;
            long d = (long)denominator * scalePerIteration;
            if (n > (long)Integer.MAX_VALUE || d > (long)Integer.MAX_VALUE) {
                // too large, cannot continue
                break;
            }
            nominator = (int)n;
            denominator = (int)d;
            final MicroUnits result = startNumber.multAndDivide(nominator, denominator, roundingMode);
            // obtain the difference
            final long diff = result.getMantissa() - compareResult.getMantissa();
            if (Math.abs(diff) > EPSILON) {
                // invoke an assertion which definitely fails
                Assertions.assertEquals(compareResult, result, "ratio failed at " + nominator + " / " + denominator);
            }
        }
    }

    @Test
    public void testRatios35() throws Exception {
        testRatios(START_VALUE, 3, 5, 2, RoundingMode.HALF_EVEN);
    }

    @Test
    public void testRatios27() throws Exception {
        testRatios(START_VALUE, 2, 7, 3, RoundingMode.HALF_EVEN);
    }
}
