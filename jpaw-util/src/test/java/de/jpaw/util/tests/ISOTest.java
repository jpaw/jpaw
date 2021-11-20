package de.jpaw.util.tests;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public class ISOTest {

    @Test
    public void testJavaCurrencies() throws Exception {
        List<CurrencyData> all = JavaCurrencyDataProvider.INSTANCE.getAll();
        System.out.println("I got " + all.size() + " currencies from standard Java (" + System.getProperty("java.version") + ")");
        assert (all.size() > 200);
    }
}
