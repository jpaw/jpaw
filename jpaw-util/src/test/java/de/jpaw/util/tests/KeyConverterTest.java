package de.jpaw.util.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.jpaw.api.iso.CountryKeyConverter;
import de.jpaw.api.iso.CurrencyKeyConverter;
import de.jpaw.api.iso.LanguageKeyConverter;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public class KeyConverterTest {
    
    @DataProvider(name = "booleans")
    public Object[][] createData() {
        return new Object[][] { { Boolean.FALSE }, { Boolean.TRUE }, };
    }
    
    @Test(dataProvider = "booleans")
    public void testA2ToInt(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("DE"), 5);
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("ES"), 100 + 4 * 26 + 18);
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("XX"), 1);
    }

    @Test(dataProvider = "booleans")
    public void testIntToA2(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(5), "DE");
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(100 + 4 * 26 + 18), "ES");
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(1), "XX");
    }
    
    @Test(dataProvider = "booleans")
    public void testA3ToInt(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.instance);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("USD"), 2);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("TND"), 100 + 19 * 676 + 13 * 26 + 3);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("XXX"), 1);
    }

    @Test(dataProvider = "booleans")
    public void testIntToA3(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.instance);
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(2), "USD");
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(100 + 19 * 676 + 13 * 26 + 3), "TND");
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(1), "XXX");
    }
    
    @Test(dataProvider = "booleans")
    public void testLangToInt(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("es"), 2);               // frequent
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("bb"), 60 + 2 * 32 + 2); // uncached
        Assert.assertEquals(LanguageKeyConverter.languageCodeToInt("xx"), 1);               // default, smallest value
    }

    @Test(dataProvider = "booleans")
    public void testIntToLang(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(2), "es");
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(60 + 2 * 32 + 2), "bb");
        Assert.assertEquals(LanguageKeyConverter.intToLanguageCode(1), "xx");
    }
}
