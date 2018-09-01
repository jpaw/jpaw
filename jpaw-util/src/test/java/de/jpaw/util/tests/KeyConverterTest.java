package de.jpaw.util.tests;


import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.jpaw.api.iso.CountryKeyConverter;
import de.jpaw.api.iso.CurrencyKeyConverter;
import de.jpaw.api.iso.LanguageKeyConverter;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

@RunWith(Parameterized.class)
public class KeyConverterTest {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { Boolean.FALSE }, { Boolean.TRUE } });
    }

    @Parameter
    public boolean initCache;

    @Test
    public void testA2ToInt() throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("DE"), 5);
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("ES"), 100 + 4 * 26 + 18);
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("XX"), 1);
    }

    @Test
    public void testIntToA2() throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(5), "DE");
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(100 + 4 * 26 + 18), "ES");
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(1), "XX");
    }

    @Test
    public void testA3ToInt() throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.instance);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("USD"), 2);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("TND"), 100 + 19 * 676 + 13 * 26 + 3);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("XXX"), 1);
    }

    @Test
    public void testIntToA3() throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.instance);
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(2), "USD");
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(100 + 19 * 676 + 13 * 26 + 3), "TND");
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(1), "XXX");
    }

    @Test
    public void testLangToInt() throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("es"), 2);               // frequent
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("bb"), 60 + 2 * 32 + 2); // uncached
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("xx"), 1);               // default, smallest value
    }

    @Test
    public void testIntToLang() throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(2), "es");
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(60 + 2 * 32 + 2), "bb");
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(1), "xx");
    }
}
