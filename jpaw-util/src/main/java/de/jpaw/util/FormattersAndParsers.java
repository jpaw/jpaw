package de.jpaw.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A collection of parsers and formatters for JSON and XML input / output.
 * These formats allow certain control (parser strictness settings, output formatting).
 */
public final class FormattersAndParsers {
    public static final int LENGTH_OF_ISO_DATE = 10;   // yyyy-mm-dd

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private FormattersAndParsers() { }

    // zero-GC implementation of appendable.append(String.format("%02d", n));
    private static void append2Digits(final Appendable sb, final int n) throws IOException {
        sb.append(DIGITS[(n / 10) % 10]);
        sb.append(DIGITS[n % 10]);
    }

    // zero-GC implementation of appendable.append(String.format(".%03d", millis));
    public static void appendMilliseconds(final Appendable sb, final int millis) throws IOException {
        sb.append('.');
        sb.append(DIGITS[millis / 100]);
        append2Digits(sb, millis);
    }

    // zero-GC implementation of appendable.append(String.format("%04d-%02d-%02d", ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
    public static void appendLocalDate(final Appendable sb, final LocalDate ld) throws IOException {
        append2Digits(sb, ld.getYear() / 100);
        append2Digits(sb, ld.getYear());
        sb.append('-');
        append2Digits(sb, ld.getMonthValue());
        sb.append('-');
        append2Digits(sb, ld.getDayOfMonth());
    }

    // zero-GC implementation of appendable.append(String.format("%02d:%02d:%02d%s", lt.getHour(), lt.getMinute(), lt.getSecond(), millis);
    public static void appendLocalTime(final Appendable sb, final LocalTime lt, final boolean outputFractionalSeconds, final boolean alwaysOutputFractionals)
              throws IOException {
        append2Digits(sb, lt.getHour());
        sb.append(':');
        append2Digits(sb, lt.getMinute());
        sb.append(':');
        append2Digits(sb, lt.getSecond());

        if (outputFractionalSeconds) {
            final int millis = lt.getNano() / 1000000;
            if (alwaysOutputFractionals || millis != 0) {
                appendMilliseconds(sb, millis);
            }
        }
    }

    public static void appendLocalDateTime(final Appendable sb, final LocalDateTime ldt, final boolean outputFractionalSeconds,
         final boolean alwaysOutputFractionals, final String addSuffixTimezone) throws IOException {
        appendLocalDate(sb, ldt.toLocalDate());
        sb.append('T');
        appendLocalTime(sb, ldt.toLocalTime(), outputFractionalSeconds, alwaysOutputFractionals);
        if (addSuffixTimezone != null) {
            sb.append(addSuffixTimezone);
        }
    }

    public static LocalTime parseLocalTime(final String time, final boolean ignoreFractionalSeconds) {
        if (ignoreFractionalSeconds) {
            final int pos = time.indexOf('.');
            if (pos > 0) {
                // skipping UTC suffix is implied...
                return LocalTime.parse(time.substring(0, pos));
            }
        }
        return LocalTime.parse(time);
    }

    public static LocalDateTime parseLocalDateTime(final String dateTime, final boolean ignoreFractionalSeconds, final boolean tolerateSuffixUTC,
      final boolean tolerateMissingTime) {
        if (tolerateMissingTime) {
            if (dateTime.length() == LENGTH_OF_ISO_DATE) {
                final LocalDate justDate = LocalDate.parse(dateTime);
                return LocalDateTime.of(justDate, LocalTime.MIDNIGHT);
            }
        }
        if (ignoreFractionalSeconds) {
            final int pos = dateTime.indexOf('.');
            if (pos > 0) {
                // skipping UTC suffix is implied...
                return LocalDateTime.parse(dateTime.substring(0, pos));
            }
        }
        if (tolerateSuffixUTC) {
            final int len = dateTime.length();
            if (len > 0 && dateTime.charAt(len - 1) == 'Z') {
                return LocalDateTime.parse(dateTime.substring(0, len - 1));
            }
        }
        return LocalDateTime.parse(dateTime);
    }
}
