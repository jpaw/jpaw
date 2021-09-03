package de.jpaw.fixedpoint.money.tests;

import org.testng.annotations.Test;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

@Test
public class TestToString {

    public void testToString() throws Exception {
        FPCurrency Euro = new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR"));

        FPAmount x = new FPAmount(Euro, 1999, 1680, 319);

        System.out.println(x);
    }

}
