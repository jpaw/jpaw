package de.jpaw.fixedpoint.tests;

import java.math.RoundingMode;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.fixedpoint.types.MicroUnits;

public class TestBigMult {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestBigMult.class);

    @Test
    public void testBigMult() throws Exception {
        final MicroUnits netAmount = MicroUnits.valueOf(3380);
        final MicroUnits usedTaxPercentage = MicroUnits.valueOf(7);

        for (int i = 8; i < 10000; i *= 5) {
            final MicroUnits amount = netAmount.multiply(i);
            final MicroUnits taxAmount = amount.multiplyAndRound(usedTaxPercentage, 0, RoundingMode.HALF_EVEN).divide(100);
            LOGGER.info("tax rate of {} is {}", amount, taxAmount);
        }
    }
}
