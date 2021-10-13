package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.VariableUnits;


public class TestArithmetic {

    @Test
    public void testDivision() throws Exception {
        VariableUnits gross = VariableUnits.of(1999, 2);
        MilliUnits taxed = MilliUnits.of(1190);   // 119%
        VariableUnits net = gross.divide(taxed, RoundingMode.HALF_EVEN);
        Assertions.assertEquals(0, net.compareTo(VariableUnits.of(1680, 2)));
    }

    @Test
    public void testDivisionByInt() throws Exception {
        MicroUnits big1 = MicroUnits.valueOf(15);
        MicroUnits big2 = MicroUnits.valueOf(25);
        long million = 1_000_000L;
        long tenMillion = 10_000_000L;
        Assertions.assertEquals(MicroUnits.of(15), big1.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(25), big2.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(2), big1.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
        Assertions.assertEquals(MicroUnits.of(2), big2.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
    }

    @Test
    public void testDivisionByIntWithRound() throws Exception {
        MicroUnits big1 = MicroUnits.valueOf(1);
        MicroUnits big2 = MicroUnits.valueOf(2);
        Assertions.assertEquals(MicroUnits.of(333000), big1.divideAndRound(3, 3, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(333000), big1.divideAndRound(3, 3, RoundingMode.HALF_EVEN));
        Assertions.assertEquals(MicroUnits.of(666000), big2.divideAndRound(3, 3, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(667000), big2.divideAndRound(3, 3, RoundingMode.HALF_EVEN));
    }

    @Test
    public void testDivisionByIntNegative() throws Exception {
        MicroUnits big1 = MicroUnits.valueOf(-15);
        MicroUnits big2 = MicroUnits.valueOf(-25);
        long million = 1_000_000L;
        long tenMillion = 10_000_000L;
        Assertions.assertEquals(MicroUnits.of(-15), big1.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(-25), big2.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(-2), big1.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
        Assertions.assertEquals(MicroUnits.of(-2), big2.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
    }

    @Test
    public void testDivisionByIntNegativeDivisor() throws Exception {
        MicroUnits big1 = MicroUnits.valueOf(15);
        MicroUnits big2 = MicroUnits.valueOf(25);
        long million = -1_000_000L;
        long tenMillion = -10_000_000L;
        Assertions.assertEquals(MicroUnits.of(-15), big1.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(-25), big2.divideAndRound(million, 6, RoundingMode.DOWN));
        Assertions.assertEquals(MicroUnits.of(-2), big1.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
        Assertions.assertEquals(MicroUnits.of(-2), big2.divideAndRound(tenMillion, 6, RoundingMode.HALF_EVEN));
    }

    @Test
    public void testMaxScale() throws Exception {
        MicroUnits pi4 = MicroUnits.valueOf("3.1415");
        Assertions.assertEquals(false, pi4.hasMaxScale(0), "Check for 0 decimals");
        Assertions.assertEquals(false, pi4.hasMaxScale(1), "Check for 1 decimals");
        Assertions.assertEquals(false, pi4.hasMaxScale(2), "Check for 2 decimals");
        Assertions.assertEquals(false, pi4.hasMaxScale(3), "Check for 3 decimals");
        Assertions.assertEquals(true, pi4.hasMaxScale(4), "Check for 4 decimals");
        Assertions.assertEquals(true, pi4.hasMaxScale(5), "Check for 5 decimals");
        Assertions.assertEquals(true, pi4.hasMaxScale(6), "Check for 6 decimals");
        Assertions.assertEquals(true, pi4.hasMaxScale(9), "Check for 9 decimals");
    }
}
