package de.jpaw.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A collection of parsers and formatters for JSON and XML input / output.
 * These formats allow certain control (parser strictness settings, output formatting).
 */
public class FormattersAndParsers {
    private static final char [] DIGITS = { '0', '1', '2','3', '4', '5', '6', '7', '8', '9' };

    // zero-GC implementation of appendable.append(String.format("%02d", n));
    private static void append2Digits(Appendable sb, int n) throws IOException {
        sb.append(DIGITS[(n / 10) % 10]);
        sb.append(DIGITS[n % 10]);
    }

    // zero-GC implementation of appendable.append(String.format(".%03d", millis));
    public static void appendMilliseconds(Appendable sb, int millis) throws IOException {
        sb.append('.');
        sb.append(DIGITS[millis / 100]);
        append2Digits(sb, millis);
    }

    // zero-GC implementation of appendable.append(String.format("%04d-%02d-%02d", ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
    public static void appendLocalDate(Appendable sb, LocalDate ld) throws IOException {
        append2Digits(sb, ld.getYear() / 100);
        append2Digits(sb, ld.getYear());
        sb.append('-');
        append2Digits(sb, ld.getMonthValue());
        sb.append('-');
        append2Digits(sb, ld.getDayOfMonth());
    }

    // zero-GC implementation of appendable.append(String.format("%02d:%02d:%02d%s", lt.getHour(), lt.getMinute(), lt.getSecond(), millis);
    public static void appendLocalTime(Appendable sb, LocalTime lt, boolean outputFractionalSeconds) throws IOException {
        append2Digits(sb, lt.getHour());
        sb.append(':');
        append2Digits(sb, lt.getMinute());
        sb.append(':');
        append2Digits(sb, lt.getSecond());

        if (outputFractionalSeconds) {
            final int millis = lt.getNano() / 1000000;
            if (millis != 0) {
                appendMilliseconds(sb, millis);
            }
        }
    }

    public static void appendLocalDateTime(Appendable sb, LocalDateTime ldt, boolean outputFractionalSeconds, String addSuffixTimezone) throws IOException {
        appendLocalDate(sb, ldt.toLocalDate());
        sb.append('T');
        appendLocalTime(sb, ldt.toLocalTime(), outputFractionalSeconds);
        if (addSuffixTimezone != null) {
            sb.append(addSuffixTimezone);
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateTime, boolean ignoreFractionalSeconds, boolean tolerateSuffixUTC) {
        if (ignoreFractionalSeconds) {
            int pos = dateTime.indexOf('.');
            if (pos > 0) {
                // skipping UTC suffix is implied...
                return LocalDateTime.parse(dateTime.substring(0, pos));
            }
        }
        if (tolerateSuffixUTC) {
            int len = dateTime.length();
            if (len > 0 && dateTime.charAt(len - 1) == 'Z') {
                return LocalDateTime.parse(dateTime.substring(0, len-1));
            }
        }
        return LocalDateTime.parse(dateTime);
    }
}
