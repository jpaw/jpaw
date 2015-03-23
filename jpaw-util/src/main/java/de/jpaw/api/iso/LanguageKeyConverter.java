package de.jpaw.api.iso;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.jpaw.util.CharTestsASCII;

/** Utility methods to convert String language / currency codes into a numeric code and vice versa. */
public class LanguageKeyConverter {
    static private final int OFFSET_COMPUTED_2 = 60;            // offset for 2 letter codes
    static private final int OFFSET_COMPUTED_5 = 60 + 32*32;    // offset for 5 letter codes
    static private final String [] FREQUENT_LANGUAGE_CODES = {
        "xx", "es", "en", "hi", "zh", "zh_CN", "zh_TW", "de", "fr", "it", "pt", "en_GB", "en_US"    // "xxx" for default
    };
    static private final ConcurrentMap<String, Integer> FREQUENT_LANGUAGE_CODES_MAP = new ConcurrentHashMap<String,Integer>(400);
    static {
        for (int i = 0; i < FREQUENT_LANGUAGE_CODES.length; ++i)
            FREQUENT_LANGUAGE_CODES_MAP.put(FREQUENT_LANGUAGE_CODES[i], Integer.valueOf(i + 1));
    }


    /** convert a language code string into a number, or return 0 if the code does not conform to the spec.
     * Frequently occurring codes will get small numbers. */
    public static int languageCodeToInt(String languageCode) {
        Integer frequent = FREQUENT_LANGUAGE_CODES_MAP.get(languageCode);
        if (frequent != null)
            return frequent.intValue();
        // error check
        if (languageCode.length() < 2 ||
                !CharTestsASCII.isAsciiLowerCase(languageCode.charAt(0)) ||
                !CharTestsASCII.isAsciiLowerCase(languageCode.charAt(1))) {
            // not OK
            return 0;
        }
        if (languageCode.length() == 2) {
            return OFFSET_COMPUTED_2 + ((languageCode.charAt(0) & 0x1f) << 5) + (languageCode.charAt(1) & 0x1f);
        }
        if (languageCode.charAt(2) != '_' || languageCode.length() != 5 ||
            !CharTestsASCII.isAsciiUpperCase(languageCode.charAt(3)) ||
            !CharTestsASCII.isAsciiUpperCase(languageCode.charAt(4))) {
            return 0;
        }
        // default: by formula
        return OFFSET_COMPUTED_5
            + ((languageCode.charAt(0) & 0x1f) << 15)
            + ((languageCode.charAt(1) & 0x1f) << 10)
            + ((languageCode.charAt(3) & 0x1f) <<  5)
            + ((languageCode.charAt(4) & 0x1f) );
    }

    public static String intToLanguageCode(int languageCodeIndex) {
        if (languageCodeIndex <= 0)
            return null;  // error
        if (languageCodeIndex <= FREQUENT_LANGUAGE_CODES.length)
            return FREQUENT_LANGUAGE_CODES[languageCodeIndex-1];
        if (languageCodeIndex < OFFSET_COMPUTED_2 || languageCodeIndex >= OFFSET_COMPUTED_5 + (1 << 20))
            return null;  // error
        if (languageCodeIndex < OFFSET_COMPUTED_5) {
            // short form
            languageCodeIndex -= OFFSET_COMPUTED_2;
            return String.valueOf((char)('a' + (languageCodeIndex >> 5) - 1))
                 + String.valueOf((char)('a' + (languageCodeIndex & 0x1f) - 1));
        }
        // long form
        languageCodeIndex -= OFFSET_COMPUTED_5;
        return String.valueOf((char)('a' - 1 + (languageCodeIndex >> 15)))
             + String.valueOf((char)('a' - 1 + ((languageCodeIndex >> 10) & 0x1f)))
             + "_"
             + String.valueOf((char)('A' - 1 + ((languageCodeIndex >> 5) & 0x1f)))
             + String.valueOf((char)('A' - 1 + ((languageCodeIndex) & 0x1f)));
    }

    /** Fill cache entries for all known languages.
     * If called, subsequent String construction and resulting GC overhead can be avoided. */
    public static void populateCache() {
        final String [] languages = Locale.getISOLanguages();
        for (int i = 0; i < languages.length; ++i) {
            String languageCode = languages[i];
            Integer code = FREQUENT_LANGUAGE_CODES_MAP.get(languageCode);
            if (code == null) {
                // not yet in cache
                int newCode = languageCodeToInt(languageCode);
                if (newCode > 0) {
                    // valid code: store it with the predictable index
                    FREQUENT_LANGUAGE_CODES_MAP.putIfAbsent(languageCode, Integer.valueOf(newCode));
                }
            }
        }
    }
}
