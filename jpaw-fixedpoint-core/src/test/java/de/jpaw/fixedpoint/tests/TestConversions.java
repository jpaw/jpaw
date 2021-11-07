package de.jpaw.fixedpoint.tests;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.MicroUnits;

public class TestConversions {

    @Test
    public void testFromConversions() throws Exception {
        MicroUnits fromLong = MicroUnits.valueOf(2);
        MicroUnits fromDouble = MicroUnits.valueOf(2.0);
        MicroUnits fromString = MicroUnits.valueOf("2.0");
        MicroUnits fromBigDecimal = MicroUnits.valueOf(BigDecimal.valueOf(2));
        MicroUnits fromMantissa = MicroUnits.of(2_000_000L);

        Assertions.assertEquals(fromMantissa, fromBigDecimal, "from BigDecimal");
        Assertions.assertEquals(fromMantissa, fromString, "from String");
        Assertions.assertEquals(fromMantissa, fromDouble, "from double");
        Assertions.assertEquals(fromMantissa, fromLong, "from long");
    }

    @Test
    public void testToConversions() throws Exception {
        MicroUnits value = MicroUnits.valueOf(2);

        Assertions.assertEquals("2", value.toString(), "to String");
        Assertions.assertEquals(BigDecimal.valueOf(2).setScale(6), value.toBigDecimal(), "to BigDecimal");
        Assertions.assertEquals(2, value.intValue(), "to int");
        Assertions.assertEquals(2.0, value.doubleValue(), "to double");
        Assertions.assertEquals(2_000_000L, value.getMantissa(), "to Mantissa");
    }
}
