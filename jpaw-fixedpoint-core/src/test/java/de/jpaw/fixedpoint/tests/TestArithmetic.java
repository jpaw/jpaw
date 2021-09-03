package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.testng.annotations.Test;

import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.VariableUnits;


@Test
public class TestArithmetic {

    public void testDivision() throws Exception {
        VariableUnits gross = VariableUnits.of(1999, 2);
        MilliUnits taxed = MilliUnits.of(1190);   // 119%
        VariableUnits net = gross.divide(taxed, RoundingMode.HALF_EVEN);
        assert(net.compareTo(VariableUnits.of(1680, 2)) == 0);
    }

}
