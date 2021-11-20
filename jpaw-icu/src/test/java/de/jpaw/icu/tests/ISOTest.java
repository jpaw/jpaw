package de.jpaw.icu.tests;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.ibm.icu.util.ULocale;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.icu.impl.ICUCurrencyDataProvider;
import de.jpaw.util.CharTestsASCII;

public class ISOTest {
    private static int is2LetterLocale(String s) {
        return (s.length() == 2 && CharTestsASCII.isAsciiLowerCase(s.charAt(0)) && CharTestsASCII.isAsciiLowerCase(s.charAt(1))) ? 1 : 0;
    }
    private static int is5CharLocale(String s) {
        return (s.length() == 5
            && CharTestsASCII.isAsciiLowerCase(s.charAt(0))
            && CharTestsASCII.isAsciiLowerCase(s.charAt(1))
            && s.charAt(2) == '_'
            && CharTestsASCII.isAsciiUpperCase(s.charAt(3))
            && CharTestsASCII.isAsciiUpperCase(s.charAt(4))) ? 1 : 0;
    }

    @Test
    public void testJavaCurrencies() throws Exception {
        List<CurrencyData> all = JavaCurrencyDataProvider.INSTANCE.getAll();
        System.out.println("I got " + all.size() + " currencies from Java");
        assert (all.size() > 200);   // jdk 1.8.031: 220
    }

    @Test
    public void testJavaCountries() throws Exception {
        String[] all = Locale.getISOCountries();
        System.out.println("I got " + all.length + " countries from Java");
        assert (all.length > 200);   // jdk 1.8.031: 250
    }

    @Test
    public void testJavaLanguages() throws Exception {
        String[] all = Locale.getISOLanguages();
        System.out.println("I got " + all.length + " languages from Java");
        assert (all.length > 150);   // jdk 1.8.031: 188
    }

    @Test
    public void testJavaLocales() throws Exception {
        Locale[] all = Locale.getAvailableLocales();
        System.out.println("I got " + all.length + " locales from Java");
        assert (all.length > 150);   // jdk 1.8.031: 160  ?? less locales than languages? 44, 108

        int sum2 = 0;
        int sum5 = 0;
        for (int i = 0; i < all.length; ++i) {
            String s = all[i].toString();
            sum2 += is2LetterLocale(s);
            sum5 += is5CharLocale(s);
            if (is2LetterLocale(s) == 0 && is5CharLocale(s) == 0)
                System.out.println("Java nonstd locale name: " + s);
        }
        System.out.println("Java 2 char locales: " + sum2);
        System.out.println("Java 5 char locales: " + sum5);
    }

    @Test
    public void testJavaCurrenciesWithMoreDecimals() throws Exception {
        List<CurrencyData> all = JavaCurrencyDataProvider.INSTANCE.getAll();
        for (CurrencyData cd: all) {
            if (cd.getDefaultFractionDigits() > 2)
                System.out.println(cd.getCurrencyCode() + " has " + cd.getDefaultFractionDigits() + " decimals (" + cd.getDisplayName() + ")");
        }
    }

    @Test
    public void testICUCurrencies() throws Exception {
        List<CurrencyData> all = ICUCurrencyDataProvider.INSTANCE.getAll();
        System.out.println("I got " + all.size() + " currencies from ICU");
        assert (all.size() > 200);   // ICU 54.1.1: 296
    }

    @Test
    public void testICUCountries() throws Exception {
        String[] all = ULocale.getISOCountries();
        System.out.println("I got " + all.length + " countries from ICU");
        assert (all.length > 200);   // ICU 54.1.1: 249  ?? less countries than standard Java?
    }

    @Test
    public void testICULanguages() throws Exception {
        String[] all = ULocale.getISOLanguages();
        System.out.println("I got " + all.length + " languages from ICU");
        assert (all.length > 150);   // ICU 54.1.1: 559
    }

    @Test
    public void testICULocales() throws Exception {
        ULocale[] all = ULocale.getAvailableLocales();
        System.out.println("I got " + all.length + " locales from ICU");
        assert (all.length > 150);   // ICU 54.1.1: 684, 118, 356

        int sum2 = 0;
        int sum5 = 0;
        for (int i = 0; i < all.length; ++i) {
            String s = all[i].getName();
            sum2 += is2LetterLocale(s);
            sum5 += is5CharLocale(s);
        }
        System.out.println("ICU 2 char locales: " + sum2);
        System.out.println("ICU 5 char locales: " + sum5);
    }
}
