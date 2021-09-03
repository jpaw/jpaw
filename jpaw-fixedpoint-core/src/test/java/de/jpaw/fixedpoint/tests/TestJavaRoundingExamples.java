package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.FixedPointNative;

// test the the examples listed in the Javadoc: http://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html

@Test
public class TestJavaRoundingExamples {
    private static final long EXCEPTION = 99999;
    private static RoundingMode [] modes = { null,
        RoundingMode.UP, RoundingMode.DOWN, RoundingMode.CEILING, RoundingMode.FLOOR, RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN, RoundingMode.UNNECESSARY
    };
    //                                     UP DOWN CEIL FLOOR  HU   HD   HE  UNNECESSARY
    private static long [] test00 = {  55,  6,   5,   6,   5,   6,   5,   6, EXCEPTION };
    private static long [] test01 = {  25,  3,   2,   3,   2,   3,   2,   2, EXCEPTION };
    private static long [] test02 = {  16,  2,   1,   2,   1,   2,   2,   2, EXCEPTION };
    private static long [] test03 = {  11,  2,   1,   2,   1,   1,   1,   1, EXCEPTION };
    private static long [] test04 = {  10,  1,   1,   1,   1,   1,   1,   1,  1 };
    private static long [] test05 = { -10, -1,  -1,  -1,  -1,  -1,  -1,  -1, -1 };
    private static long [] test06 = { -11, -2,  -1,  -1,  -2,  -1,  -1,  -1, EXCEPTION };
    private static long [] test07 = { -16, -2,  -1,  -1,  -2,  -2,  -2,  -2, EXCEPTION };
    private static long [] test08 = { -25, -3,  -2,  -2,  -3,  -3,  -2,  -2, EXCEPTION };
    private static long [] test09 = { -55, -6,  -5,  -5,  -6,  -6,  -5,  -6, EXCEPTION };
    private static long [] [] tests = {
        test00, test01, test02, test03, test04, test05, test06, test07, test08, test09
    };

    public void testNativeRounding() throws Exception {
        for (int testcase = 0; testcase < tests.length; ++testcase) {
            long [] test = tests[testcase];
            assert(modes.length == test.length);  // detect typos in test case
            for (int i = 1; i < modes.length; ++i) {
                if (test[i] != EXCEPTION) {
                    long result = FixedPointNative.multiply_and_scale(test[0], 1, 1, modes[i]);
                    Assert.assertEquals(result, test[i],
                            "Test " + testcase + ", mode " + modes[i].name());
                } else {
                    try {
                        FixedPointNative.multiply_and_scale(test[0], 1, 1, modes[i]);
                        throw new Exception("Case " + testcase + ", mode " + modes[i].name() + " should have thrown an exception");
                    } catch (ArithmeticException e) {
                        // expected this!
                    }
                }
            }
        }
    }

    public void testJavaRounding() throws Exception {
        for (int testcase = 0; testcase < tests.length; ++testcase) {
            long [] test = tests[testcase];
            assert(modes.length == test.length);  // detect typos in test case
            for (int i = 1; i < modes.length; ++i) {
                if (test[i] != EXCEPTION) {
                    long result = FixedPointBase.divide_longs(test[0], 10, modes[i]);
                    Assert.assertEquals(result, test[i],
                            "Test " + testcase + ", mode " + modes[i].name());
                } else {
                    try {
                        FixedPointBase.divide_longs(test[0], 10, modes[i]);
                        throw new Exception("Case " + testcase + ", mode " + modes[i].name() + " should have thrown an exception");
                    } catch (ArithmeticException e) {
                        // expected this!
                    }
                }
            }
        }
    }
}
