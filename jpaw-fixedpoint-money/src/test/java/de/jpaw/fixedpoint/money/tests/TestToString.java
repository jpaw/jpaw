package de.jpaw.fixedpoint.money.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

public class TestToString {

    @Test
    public void testToString() throws Exception {
        FPCurrency Euro = new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR"));

        FPAmount x = new FPAmount(Euro, 1999, 1680, 319);

        System.out.println(x);
        Assertions.assertEquals("19.99 EUR [16.8, 3.19]", x.toString());
    }
}
