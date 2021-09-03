package de.jpaw.fixedpoint.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.types.Hundreds;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.fixedpoint.types.VariableUnits;


@Test
public class TestStringConversions {
    static private class TestCase {
        public TestCase(String stringRep, FixedPointBase<?> data) {
            super();
            this.stringRep = stringRep;
            this.data = data;
        }
        final String stringRep;
        final FixedPointBase<?> data;
    }

    static private TestCase [] testCasesToString = {
        new TestCase("1", Units.valueOf(1)),
        new TestCase("-1.000", MilliUnits.valueOf(-1)),
        new TestCase("3.14", Hundreds.valueOf(3.14)),           // also tests double to Hundreds
        new TestCase("3.14", Hundreds.valueOf(3.13888)),        // also tests double to Hundreds
        new TestCase("3.14", Hundreds.valueOf(3.14159)),        // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.14)),         // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.13888)),      // also tests double to Hundreds
        new TestCase("-3.14", Hundreds.valueOf(-3.14159)),      // also tests double to Hundreds
        new TestCase("19.80", new Hundreds(1980)),
        new TestCase("19.99", new Hundreds(1999)),
        new TestCase("-19.80", new Hundreds(-1980)),
        new TestCase("-19.99", new Hundreds(-1999))
    };

    public void testToString() throws Exception {

        for (int i = 0; i < testCasesToString.length; ++i) {
            TestCase t = testCasesToString[i];
            Assert.assertEquals(t.data.toString(), t.stringRep);
        }
    }


    static private TestCase [] testCasesParse = {
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

    public void testParse() throws Exception {

        for (int i = 0; i < testCasesParse.length; ++i) {
            TestCase t = testCasesParse[i];
            if (t.data != null) {
                Assert.assertEquals(MilliUnits.valueOf(t.stringRep), t.data);
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


    public void testVariableConversions() throws Exception {
        Assert.assertEquals(VariableUnits.valueOf("19.80").getScale(), 2);
        Assert.assertEquals(VariableUnits.valueOf("-19.80").getScale(), 2);
    }
}
