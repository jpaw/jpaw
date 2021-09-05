package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

}
