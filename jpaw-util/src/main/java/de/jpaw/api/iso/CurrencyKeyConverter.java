package de.jpaw.api.iso;

import java.util.HashMap;
import java.util.Map;

import de.jpaw.util.CharTestsASCII;

public class CurrencyKeyConverter {
    static private final int OFFSET_COMPUTED = 100; 
    static private final int NUM_COMPUTED = 26*26*26; 
    static private final String [] FREQUENT_CURRENCY_CODES_A3 = {            // sorted by descending gross domestic product, 2012
        "XXX", "USD", "CNY", "JPY", "EUR", "BRR", "RUB", "INR", "GBP", "CHF", "HKD", "AUD", "CAD" // plus "XXX" for default
    };
    static private final Map<String, Integer> FREQUENT_CURRENCY_CODES_A3_MAP = new HashMap<String,Integer>(20);
    static {
        for (int i = 0; i < FREQUENT_CURRENCY_CODES_A3.length; ++i)
            FREQUENT_CURRENCY_CODES_A3_MAP.put(FREQUENT_CURRENCY_CODES_A3[i], Integer.valueOf(i + 1));
    }
    
    
    /** convert a country code string into a number, or return 0 if the code does not conform to the spec.
     * Frequently occurring codes will get small numbers.
     * The range is within [1..776) (10 bit) */
    public static int currencyCodeA3ToInt(String currencyCode) {
        Integer frequent = FREQUENT_CURRENCY_CODES_A3_MAP.get(currencyCode);
        if (frequent != null)
            return frequent.intValue();
        // error check
        if (currencyCode.length() != 3 ||
                !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(0)) ||
                !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(1)) ||
                !CharTestsASCII.isAsciiUpperCase(currencyCode.charAt(2)))
                return 0;
        // default: by formula
        return OFFSET_COMPUTED + (currencyCode.charAt(0) - 'A') * 676 + (currencyCode.charAt(1) - 'A') * 26 + (currencyCode.charAt(2) - 'A');
    }
    
    public static String intToCurrencyCodeA3(int currencyCodeIndex) {
        if (currencyCodeIndex <= 0)
            return null;  // error
        if (currencyCodeIndex <= FREQUENT_CURRENCY_CODES_A3.length)
            return FREQUENT_CURRENCY_CODES_A3[currencyCodeIndex-1];
        if (currencyCodeIndex < OFFSET_COMPUTED || currencyCodeIndex >= (OFFSET_COMPUTED + NUM_COMPUTED))
            return null;  // error
        currencyCodeIndex -= OFFSET_COMPUTED;
        return String.valueOf((char)('A' + currencyCodeIndex / 676))
                + String.valueOf((char)('A' + (currencyCodeIndex / 26) % 26))
                + String.valueOf((char)('A' + currencyCodeIndex % 26));
    }

}
