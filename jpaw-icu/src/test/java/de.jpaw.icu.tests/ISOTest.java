package de.jpaw.icu.tests;

import java.util.List;

import org.testng.annotations.Test;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.icu.impl.ICUCurrencyDataProvider;

public class ISOTest {

    @Test
    public void testICUCurrencies() throws Exception {
        List<CurrencyData> all = ICUCurrencyDataProvider.instance.getAll();
        System.out.println("I got " + all.size() + " currencies from ICU");
        assert(all.size() > 200);
    }

}
