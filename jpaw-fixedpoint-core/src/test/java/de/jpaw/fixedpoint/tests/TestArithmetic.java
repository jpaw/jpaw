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
