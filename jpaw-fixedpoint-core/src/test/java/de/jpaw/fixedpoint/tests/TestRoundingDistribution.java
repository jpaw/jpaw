package de.jpaw.fixedpoint.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.Units;


public class TestRoundingDistribution {

    private void runTestScaleDown(long [] unscaled, long [] scaled, int scale) {
        // validate test case
        long sum = 0;
        for (int i = 1; i < unscaled.length; ++i)
            sum += unscaled[i];
        Assertions.assertEquals(unscaled[0], sum, "SUM");
        long [] actuals = Units.ZERO.roundWithErrorDistribution(unscaled, 2);
        Assertions.assertArrayEquals(scaled, actuals);
    }


    @Test
    public void testPositiveVector() throws Exception {
        final long [] h = { 297, 148, 149 };
        final long [] i = { 3, 1, 2 };
        runTestScaleDown(h, i, 2);
    }

    @Test
    public void testMixedSignVector() throws Exception {
        final long [] h = { 8, -144, 152 };
        final long [] i = { 0, -1, 1 };
        runTestScaleDown(h, i, 2);
    }

    @Test
    public void testMiniAmountsVector() throws Exception {
        final long [] h = { 339, 49, 48, 49, 47, 48, 51, 47 };
        final long [] i = {   3,  1,  0,  1,  0,  0,  1,  0 };
        runTestScaleDown(h, i, 2);
    }
}
