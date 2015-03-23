package de.jpaw.api.iso;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.jpaw.util.CharTestsASCII;

/** Utility methods to convert String country / currency codes into a numeric code and vice versa. */
public class CountryKeyConverter {
    static private final int OFFSET_COMPUTED = 100;
    static private final String [] FREQUENT_COUNTRY_CODES_A2 = {            // sorted by descending gross domestic product, 2012
        "XX", "US", "CN", "JP", "DE", "FR", "BR", "GB", "RU", "IN", "IT"    // plus "XX" for default
    };
    static private final ConcurrentMap<String, Integer> FREQUENT_COUNTRY_CODES_A2_MAP = new ConcurrentHashMap<String,Integer>(400);
    static {
        for (int i = 0; i < FREQUENT_COUNTRY_CODES_A2.length; ++i)
            FREQUENT_COUNTRY_CODES_A2_MAP.put(FREQUENT_COUNTRY_CODES_A2[i], Integer.valueOf(i + 1));
    }


    /** convert a country code string into a number, or return 0 if the code does not conform to the spec.
     * Frequently occurring codes will get small numbers.
     * The range is within [1..776) (10 bit) */
    public static int countryCodeA2ToInt(String countryCode) {
        Integer frequent = FREQUENT_COUNTRY_CODES_A2_MAP.get(countryCode);
        if (frequent != null)
            return frequent.intValue();
        // error check
        if (countryCode.length() != 2 ||
                !CharTestsASCII.isAsciiUpperCase(countryCode.charAt(0)) ||
                !CharTestsASCII.isAsciiUpperCase(countryCode.charAt(1)))
                return 0;
        // default: by formula
        return OFFSET_COMPUTED + (countryCode.charAt(0) - 'A') * 26 + countryCode.charAt(1) - 'A';
    }

    public static String intToCountryCodeA2(int countryCodeIndex) {
        if (countryCodeIndex <= 0)
            return null;  // error
        if (countryCodeIndex <= FREQUENT_COUNTRY_CODES_A2.length)
            return FREQUENT_COUNTRY_CODES_A2[countryCodeIndex-1];
        if (countryCodeIndex < OFFSET_COMPUTED || countryCodeIndex >= 776)
            return null;  // error
        countryCodeIndex -= OFFSET_COMPUTED;
        return String.valueOf((char)('A' + countryCodeIndex / 26)) + String.valueOf((char)('A' + countryCodeIndex % 26));
    }

    /** Fill cache entries for all known countries.
     * If called, subsequent String construction and resulting GC overhead can be avoided. */
    public static void populateCache() {
        final String [] countries = Locale.getISOCountries();
        for (int i = 0; i < countries.length; ++i) {
            String countryCode = countries[i];
            Integer code = FREQUENT_COUNTRY_CODES_A2_MAP.get(countryCode);
            if (code == null) {
                // not yet in cache
                int newCode = countryCodeA2ToInt(countryCode);
                if (newCode > 0) {
                    // valid code: store it with the predictable index
                    FREQUENT_COUNTRY_CODES_A2_MAP.putIfAbsent(countryCode, Integer.valueOf(newCode));
                }
            }
        }
    }
}
