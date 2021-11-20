package de.jpaw.util.tests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.jpaw.api.iso.CountryKeyConverter;
import de.jpaw.api.iso.CurrencyKeyConverter;
import de.jpaw.api.iso.LanguageKeyConverter;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public class KeyConverterTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testA2ToInt(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assertions.assertEquals(CountryKeyConverter.countryCodeA2ToInt("DE"), 5);
        Assertions.assertEquals(CountryKeyConverter.countryCodeA2ToInt("ES"), 100 + 4 * 26 + 18);
        Assertions.assertEquals(CountryKeyConverter.countryCodeA2ToInt("XX"), 1);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToA2(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assertions.assertEquals(CountryKeyConverter.intToCountryCodeA2(5), "DE");
        Assertions.assertEquals(CountryKeyConverter.intToCountryCodeA2(100 + 4 * 26 + 18), "ES");
        Assertions.assertEquals(CountryKeyConverter.intToCountryCodeA2(1), "XX");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testA3ToInt(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.INSTANCE);
        Assertions.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("USD"), 2);
        Assertions.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("TND"), 100 + 19 * 676 + 13 * 26 + 3);
        Assertions.assertEquals(CurrencyKeyConverter.currencyCodeA3ToInt("XXX"), 1);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToA3(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.INSTANCE);
        Assertions.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(2), "USD");
        Assertions.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(100 + 19 * 676 + 13 * 26 + 3), "TND");
        Assertions.assertEquals(CurrencyKeyConverter.intToCurrencyCodeA3(1), "XXX");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testLangToInt(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assertions.assertEquals(LanguageKeyConverter.languageCodeToInt("es"), 2);               // frequent
        Assertions.assertEquals(LanguageKeyConverter.languageCodeToInt("bb"), 60 + 2 * 32 + 2); // uncached
        Assertions.assertEquals(LanguageKeyConverter.languageCodeToInt("xx"), 1);               // default, smallest value
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToLang(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assertions.assertEquals(LanguageKeyConverter.intToLanguageCode(2), "es");
        Assertions.assertEquals(LanguageKeyConverter.intToLanguageCode(60 + 2 * 32 + 2), "bb");
        Assertions.assertEquals(LanguageKeyConverter.intToLanguageCode(1), "xx");
    }
}
