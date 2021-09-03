package de.jpaw.fixedpoint.tests;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.fixedpoint.types.Units;


@Test
public class TestRoundingDistribution {

    private void runTestScaleDown(long [] unscaled, long [] scaled, int scale) {
        // validate test case
        long sum = 0;
        for (int i = 1; i < unscaled.length; ++i)
            sum += unscaled[i];
        Assert.assertEquals(sum, unscaled[0]);
        long [] actuals = Units.ZERO.roundWithErrorDistribution(unscaled, 2);
        assert(Arrays.equals(actuals, scaled));
    }


    public void testPositiveVector() throws Exception {
        final long [] h = { 297, 148, 149 };
        final long [] i = { 3, 1, 2 };
        runTestScaleDown(h, i, 2);
    }

    public void testMixedSignVector() throws Exception {
        final long [] h = { 8, -144, 152 };
        final long [] i = { 0, -1, 1 };
        runTestScaleDown(h, i, 2);
    }

    public void testMiniAmountsVector() throws Exception {
        final long [] h = { 339, 49, 48, 49, 47, 48, 51, 47 };
        final long [] i = {   3,  1,  0,  1,  0,  0,  1,  0 };
        runTestScaleDown(h, i, 2);
    }
}
