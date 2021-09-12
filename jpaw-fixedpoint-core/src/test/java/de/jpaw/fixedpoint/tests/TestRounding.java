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
                    assertSame(mpi.round(digits, rm).multiply(me, rm), bdpi.setScale(digits, rm).multiply(bde).setScale(6, rm), what + " (times e)");
                    assertSame(mipi.round(digits, rm).multiply(me, rm), bdmpi.setScale(digits, rm).multiply(bde).setScale(6, rm), what + " (times e, negative)");
                }
            }
        }
    }

    // assert that the fixed point number and the BigDecimal have the same value  
    void assertSame(MicroUnits m, BigDecimal bd, String where) {
        BigDecimal actual = m.toBigDecimal();
        boolean result = bd.compareTo(actual) == 0;
        if (!result) {
            System.out.println("Mismatch for " + where + ": expected " + bd + ", but got " + m);
        }
        Assertions.assertTrue(result, where);
    }
}
