package de.jpaw.fixedpoint.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.MicroUnits;

public class TestRounding {

    @Test
    public void testRounding() throws Exception {
        MicroUnits mpi = MicroUnits.valueOf(Math.PI);
        BigDecimal bdpi = BigDecimal.valueOf(Math.PI).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(mpi, bdpi, "Original value of PI");

        MicroUnits me = MicroUnits.valueOf(Math.E);
        BigDecimal bde = BigDecimal.valueOf(Math.E).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(me, bde, "Original value of E");

        MicroUnits mipi = mpi.negate();
        BigDecimal bdmpi = bdpi.negate();
        assertSame(mipi, bdmpi, "Negated value of PI");

        for (int digits = 5; digits >= 0; --digits) {
            for (RoundingMode rm : RoundingMode.values()) {
                if (rm != RoundingMode.UNNECESSARY) {
                    final String what = "Rounded to " + digits + " digits with mode " + rm.name();
                    assertSame(mpi.round(digits, rm), bdpi.setScale(digits, rm), what);
                    assertSame(mipi.round(digits, rm), bdmpi.setScale(digits, rm), what + " (negative)");

                    // now also try the multiplication
                    assertSame(mpi.round(digits, rm).multiply(me, rm), bdpi.setScale(digits, rm).multiply(bde).setScale(6, rm),
                      what + " (times e)");
                    assertSame(mipi.round(digits, rm).multiply(me, rm), bdmpi.setScale(digits, rm).multiply(bde).setScale(6, rm),
                      what + " (times e, negative)");
                }
            }
        }
    }

    // assert that the fixed point number and the BigDecimal have the same value
    private void assertSame(MicroUnits m, BigDecimal bd, String where) {
        BigDecimal actual = m.toBigDecimal();
        boolean result = bd.compareTo(actual) == 0;
        if (!result) {
            System.out.println("Mismatch for " + where + ": expected " + bd + ", but got " + m);
        }
        Assertions.assertTrue(result, where);
    }

    @Test
    public void testRoundingHalfPositive() throws Exception {
        final MicroUnits a = MicroUnits.of(995000L); // 0.995
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_DOWN), MicroUnits.of(990000L), "half down");
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_UP),   MicroUnits.ONE, "half up");
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_EVEN), MicroUnits.ONE, "half even");
    }

    @Test
    public void testRoundingHalfNegative() throws Exception {
        final MicroUnits a = MicroUnits.of(-995000L); // 0.995
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_DOWN), MicroUnits.of(-990000L), "half down");
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_UP),   MicroUnits.ONE.negate(), "half up");
        Assertions.assertEquals(a.round(2, RoundingMode.HALF_EVEN), MicroUnits.ONE.negate(), "half even");
    }

    @Test
    public void testMultiplyAndRounding() throws Exception {
        MicroUnits mpi = MicroUnits.valueOf(Math.PI);
        BigDecimal bdpi = BigDecimal.valueOf(Math.PI).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(mpi, bdpi, "Original value of PI");

        MicroUnits me = MicroUnits.valueOf(Math.E);
        BigDecimal bde = BigDecimal.valueOf(Math.E).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(me, bde, "Original value of E");

        for (int i = 6; i >= 0; --i) {
            // multiply pi by e, rounded to x digits
            for (RoundingMode rm : RoundingMode.values()) {
                if (rm != RoundingMode.UNNECESSARY) {
                    MicroUnits prod1 = mpi.multiplyAndRound(me, i, rm);
                    MicroUnits prod2 = me.multiplyAndRound(mpi, i, rm);
                    BigDecimal trueProd = bdpi.multiply(bde).setScale(i, rm);
                    assertSame(prod1, trueProd, "PI * E rounded to " + i + " digits");
                    assertSame(prod2, trueProd, "E * PI rounded to " + i + " digits");
                }
            }
        }
    }

    @Test
    public void testDivideAndRounding() throws Exception {
        MicroUnits mpi = MicroUnits.valueOf(Math.PI);
        BigDecimal bdpi = BigDecimal.valueOf(Math.PI).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(mpi, bdpi, "Original value of PI");

        MicroUnits me = MicroUnits.valueOf(Math.E);
        BigDecimal bde = BigDecimal.valueOf(Math.E).setScale(6, RoundingMode.HALF_EVEN);
        assertSame(me, bde, "Original value of E");

        for (int i = 6; i >= 0; --i) {
            // multiply pi by e, rounded to x digits
            for (RoundingMode rm : RoundingMode.values()) {
                if (rm != RoundingMode.UNNECESSARY) {
                    MicroUnits quot1 = mpi.divideAndRound(me, i, rm);
                    MicroUnits quot2 = me.divideAndRound(mpi, i, rm);
                    BigDecimal trueQuot1 = bdpi.divide(bde, i, rm);
                    BigDecimal trueQuot2 = bde.divide(bdpi, i, rm);
                    assertSame(quot1, trueQuot1, "PI / E rounded to " + i + " digits");
                    assertSame(quot2, trueQuot2, "E / PI rounded to " + i + " digits");
                }
            }
        }
    }
}
