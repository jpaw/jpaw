package de.jpaw.api.iso;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.jpaw.util.CharTestsASCII;

public final class CurrencyKeyConverter {
    private static final int OFFSET_COMPUTED = 100;
    private static final int NUM_COMPUTED = 26 * 26 * 26;  // number of possible combinations of 3 uppercase letters
    private static final String[] FREQUENT_CURRENCY_CODES_A3 = {            // sorted by descending gross domestic product, 2012
        "XXX", "USD", "CNY", "JPY", "EUR", "BRR", "RUB", "INR", "GBP", "CHF", "HKD", "AUD", "CAD" // plus "XXX" for default
    };
    private static final ConcurrentMap<String, Integer> FREQUENT_CURRENCY_CODES_A3_MAP = new ConcurrentHashMap<>(500);
    static {
        for (int i = 0; i < FREQUENT_CURRENCY_CODES_A3.length; ++i) {
            FREQUENT_CURRENCY_CODES_A3_MAP.put(FREQUENT_CURRENCY_CODES_A3[i], Integer.valueOf(i + 1));
        }
    }

    private CurrencyKeyConverter() { }

    /** convert a country code string into a number, or return 0 if the code does not conform to the spec.
     * Frequently occurring codes will get small numbers.
     * The range is within [1..17k) (31 bit) */
    public static int currencyCodeA3ToInt(final String currencyCode) {
        final Integer frequent = FREQUENT_CURRENCY_CODES_A3_MAP.get(currencyCode);
        if (frequent != null)
            return frequent.intValue();
        // error check
        if (currencyCode.length() != 3
          || !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(0))
          || !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(1))
          || !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(2))) {
            return 0;
        }
        // default: by formula
        return OFFSET_COMPUTED + (currencyCode.charAt(0) - 'A') * 676 + (currencyCode.charAt(1) - 'A') * 26 + (currencyCode.charAt(2) - 'A');
    }

    public static String intToCurrencyCodeA3(int currencyCodeIndex) {
        if (currencyCodeIndex <= 0)
            return null;  // error
        if (currencyCodeIndex <= FREQUENT_CURRENCY_CODES_A3.length)
            return FREQUENT_CURRENCY_CODES_A3[currencyCodeIndex - 1];
        if (currencyCodeIndex < OFFSET_COMPUTED || currencyCodeIndex >= (OFFSET_COMPUTED + NUM_COMPUTED))
            return null;  // error
        currencyCodeIndex -= OFFSET_COMPUTED;
        return String.valueOf((char)('A' + currencyCodeIndex / 676))
          + String.valueOf((char)('A' + (currencyCodeIndex / 26) % 26))
          + String.valueOf((char)('A' + currencyCodeIndex % 26));
    }

    /** Fill cache entries for all known currencies.
     * If called, subsequent String construction and resulting GC overhead can be avoided. */
    public static void populateCache(final CurrencyDataProvider dp) {
        for (final CurrencyData cd: dp.getAll()) {
            final String currencyCode = cd.getCurrencyCode();
            final Integer code = FREQUENT_CURRENCY_CODES_A3_MAP.get(currencyCode);
            if (code == null) {
                // not yet in cache
                final int newCode = currencyCodeA3ToInt(currencyCode);
                if (newCode > 0) {
                    // valid code: store it with the predictable index
                    FREQUENT_CURRENCY_CODES_A3_MAP.putIfAbsent(currencyCode, Integer.valueOf(newCode));
                }
            }
        }
    }
}
