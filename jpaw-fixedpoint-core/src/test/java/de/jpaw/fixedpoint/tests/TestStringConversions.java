package de.jpaw.fixedpoint.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.types.Hundreds;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.fixedpoint.types.VariableUnits;


public class TestStringConversions {
    private static class TestCase {
        TestCase(String stringRep, FixedPointBase<?> data) {
            super();
            this.stringRep = stringRep;
            this.data = data;
        }
        final String stringRep;
        final FixedPointBase<?> data;
    }

    private static TestCase[] testCasesToString = {
        new TestCase("1", Units.valueOf(1)),
        new TestCase("-1", MilliUnits.valueOf(-1)),
        new TestCase("3.14", Hundreds.valueOf(3.14)),           // also tests double to Hundreds
        new TestCase("3.14", Hundreds.valueOf(3.13888)),        // also tests double to Hundreds
        new TestCase("3.14", Hundreds.valueOf(3.14159)),        // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.14)),         // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.13888)),      // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.14159)),      // also tests double to Hundreds
        new TestCase("19.8", Hundreds.of(1980)),
        new TestCase("19.99", Hundreds.of(1999)),
        new TestCase("-19.8", Hundreds.of(-1980)),
        new TestCase("-19.99", Hundreds.of(-1999)),
        new TestCase("-0.17", Hundreds.of(-17))
    };

    @Test
    public void testToString() throws Exception {

        for (int i = 0; i < testCasesToString.length; ++i) {
            TestCase t = testCasesToString[i];
            Assertions.assertEquals(t.stringRep, t.data.toString(), " for case " + i);
        }
    }


    private static TestCase[] testCasesParse = {
        new TestCase("-1.000", MilliUnits.valueOf(-1)),
        new TestCase("3.14", MilliUnits.valueOf(3.14)),
        new TestCase("-3.14", MilliUnits.valueOf(-3.14)),
        new TestCase("-3.14159", null),
        new TestCase("-3.+14", null),
        new TestCase("-3.-14", null),
        new TestCase("-0.14", MilliUnits.valueOf(-0.14)),
        new TestCase("-0.", MilliUnits.valueOf(0)),
        new TestCase("-1.", MilliUnits.valueOf(-1)),
        new TestCase("-.1", MilliUnits.valueOf(-0.1)),
        new TestCase("-3.14000000000000000", MilliUnits.valueOf(-3.14))
    };

    @Test
    public void testParse() throws Exception {

        for (int i = 0; i < testCasesParse.length; ++i) {
            TestCase t = testCasesParse[i];
            if (t.data != null) {
                Assertions.assertEquals(MilliUnits.valueOf(t.stringRep), t.data);
            } else {
                try {
                    MilliUnits.valueOf(t.stringRep);
                    throw new Exception("Case " + i + " should have thrown an exception");
                } catch (NumberFormatException e) {
                    // expected this!
                }
            }
        }
    }


    @Test
    public void testVariableConversions() throws Exception {
        Assertions.assertEquals(2, VariableUnits.valueOf("19.80").scale());
        Assertions.assertEquals(2, VariableUnits.valueOf("-19.80").scale());
    }
}
