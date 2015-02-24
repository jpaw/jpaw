package de.jpaw.util.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.api.iso.CountryKeyConverter;
import de.jpaw.api.iso.CurrencyKeyConverter;

public class KeyConverterTest {

    @Test
    public void testA2ToInt() throws Exception {
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("DE"), 5);
        Assert.assertEquals(CountryKeyConverter.countryCodeA2ToInt("ES"), 100 + 4 * 26 + 18);
    }

    @Test
    public void testIntToA2() throws Exception {
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(5), "DE");
        Assert.assertEquals(CountryKeyConverter.intToCountryCodeA2(100 + 4 * 26 + 18), "ES");
    }
    
    @Test
    public void testA3ToInt() throws Exception {
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("USD"), 2);
        Assert.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("TND"), 100 + 19 * 676 + 13 * 26 + 3);
    }

    @Test
    public void testIntToA3() throws Exception {
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(2), "USD");
        Assert.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(100 + 19 * 676 + 13 * 26 + 3), "TND");
    }
}
