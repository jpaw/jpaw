package de.jpaw.money.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

import de.jpaw.money.BonaCurrency;

public class BonaCurrencyTest {

    @Test
    public void testRoundingDistribution() throws Exception {
        BonaCurrency eur = new BonaCurrency("EUR");
        BigDecimal vat = BigDecimal.valueOf(119).scaleByPowerOfTen(-2);

        BigDecimal gross1 = BigDecimal.valueOf(240).scaleByPowerOfTen(-2);
        BigDecimal gross2 = BigDecimal.valueOf(241).scaleByPowerOfTen(-2);
        BigDecimal total  = BigDecimal.valueOf(481).scaleByPowerOfTen(-2);

        BigDecimal tax1 = gross1.subtract(gross1.divide(vat, 6, RoundingMode.HALF_EVEN));
        BigDecimal tax2 = gross2.subtract(gross2.divide(vat, 6, RoundingMode.HALF_EVEN));
        BigDecimal taxt = total.subtract(total.divide(vat, 6, RoundingMode.HALF_EVEN));

        BigDecimal[] components = new BigDecimal[4];
        components[0] = taxt.setScale(2, RoundingMode.HALF_EVEN);
        components[1] = tax1;
        components[2] = tax2;
        components[3] = components[0].subtract(tax1).subtract(tax2);

        BigDecimal[] scaledComponents = eur.roundWithErrorDistribution(components);
        for (int i = 0; i < 4; ++i) {
            System.out.println(components[i].toPlainString() + " is rounded to " + scaledComponents[i].toPlainString());
        }
        Assert.assertEquals(scaledComponents[0], new BigDecimal("0.77"));
        Assert.assertEquals(scaledComponents[1], new BigDecimal("0.38"));
        Assert.assertEquals(scaledComponents[2], new BigDecimal("0.39"));
        Assert.assertEquals(scaledComponents[3], new BigDecimal("0.00"));
    }
}
