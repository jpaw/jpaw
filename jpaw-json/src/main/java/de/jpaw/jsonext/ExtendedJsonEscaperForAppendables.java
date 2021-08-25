package de.jpaw.jsonext;

import java.io.IOException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.jpaw.enums.AbstractByteEnumSet;
import de.jpaw.enums.AbstractIntEnumSet;
import de.jpaw.enums.AbstractLongEnumSet;
import de.jpaw.enums.AbstractShortEnumSet;
import de.jpaw.enums.AbstractStringEnumSet;
import de.jpaw.enums.AbstractStringXEnumSet;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.EnumSetMarker;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.json.BaseJsonComposer;

/**
 * A specialized JSON escaper which uses specific formats for certain types,
 * and provides convenient hooks for customization.
 *
 */
public class ExtendedJsonEscaperForAppendables extends BaseJsonComposer {
    public static String TIMEZONE_SUFFIX_FOR_LOCAL = "";  // set to "Z" if desired, but see http://javarevisited.blogspot.com/2015/03/20-examples-of-date-and-time-api-from-Java8.html
    private static final char [] DIGITS = { '0', '1', '2','3', '4', '5', '6', '7', '8', '9' };

    // if instantInMillis is true, Instants will be written as integral values in milliseconds, otherwise as second + optional fractional parts
    // see DATE_TIMESTAMPS_AS_NANOSECONDS in https://github.com/FasterXML/jackson-datatype-jsr310 for similar setting
    protected final boolean instantInMillis;

    public ExtendedJsonEscaperForAppendables(Appendable appendable) {
        super(appendable);      // default: writeNulls = true, escapeNonAscii = false
        instantInMillis = false;
    }

    public ExtendedJsonEscaperForAppendables(Appendable appendable, boolean writeNulls, boolean escapeNonASCII, boolean instantInMillis) {
        super(appendable, writeNulls, escapeNonASCII);
        this.instantInMillis = instantInMillis;
    }

    // zero-GC implementation of appendable.append(String.format("%02d", n));
    protected void append2Digits(int n) throws IOException {
        appendable.append(DIGITS[(n / 10) % 10]);
        appendable.append(DIGITS[n % 10]);
    }

    // zero-GC implementation of appendable.append(String.format(".%03d", millis));
    protected void appendMilliseconds(int millis) throws IOException {
        appendable.append('.');
        appendable.append(DIGITS[millis / 100]);
        append2Digits(millis);
    }

    // zero-GC implementation of appendable.append(String.format("%04d-%02d-%02d", ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
    protected void appendLocalDate(LocalDate ld) throws IOException {
        append2Digits(ld.getYear() / 100);
        append2Digits(ld.getYear());
        appendable.append('-');
        append2Digits(ld.getMonthValue());
        appendable.append('-');
        append2Digits(ld.getDayOfMonth());
    }

    // zero-GC implementation of appendable.append(String.format("%02d:%02d:%02d%s", lt.getHour(), lt.getMinute(), lt.getSecond(), millis);
    protected void appendLocalTime(LocalTime lt) throws IOException {
        append2Digits(lt.getHour());
        appendable.append(':');
        append2Digits(lt.getMinute());
        appendable.append(':');
        append2Digits(lt.getSecond());
        final int millis = lt.getNano() / 1000000;
        if (millis != 0) {
            appendMilliseconds(millis);
        }
    }

    // provided as a hook to allow overriding
    protected void outputEnumSet(Object obj) throws IOException {
        if (obj instanceof AbstractStringEnumSet<?>) {
            outputUnicodeNoControls(((AbstractStringEnumSet<?>)obj).getBitmap());
        } else if (obj instanceof AbstractStringXEnumSet<?>) {
            outputUnicodeNoControls(((AbstractStringXEnumSet<?>)obj).getBitmap());
        } else if (obj instanceof AbstractIntEnumSet<?>) {
            appendable.append(Integer.toString(((AbstractIntEnumSet<?>)obj).getBitmap()));
        } else if (obj instanceof AbstractLongEnumSet<?>) {
            appendable.append(Long.toString(((AbstractLongEnumSet<?>)obj).getBitmap()));
        } else if (obj instanceof AbstractByteEnumSet<?>) {
            appendable.append(Byte.toString(((AbstractByteEnumSet<?>)obj).getBitmap()));
        } else if (obj instanceof AbstractShortEnumSet<?>) {
            appendable.append(Short.toString(((AbstractShortEnumSet<?>)obj).getBitmap()));
        } else {
            throw new RuntimeException("Cannot transform enum set of type " + obj.getClass().getSimpleName() + " to JSON");
        }
    }

    // provided as a hook to allow overriding
    protected void outputTokenizableEnum(TokenizableEnum obj) throws IOException {
        outputUnicodeNoControls(obj.getToken());
    }

    // provided as a hook to allow overriding
    protected void outputNonTokenizableEnum(Enum<?> obj) throws IOException {
        outputNumber(obj.ordinal());
    }

    // provided as a hook to allow overriding
    protected void outputInstant(Instant obj) throws IOException {
        long seconds = obj.getEpochSecond();
        int millis = obj.getNano() / 1000000;
        if (instantInMillis) {
            appendable.append(Long.toString(1000L * seconds + millis));
        } else {
            appendable.append(Long.toString(seconds));
            if (millis > 0) {
                appendMilliseconds(millis);
            }
        }
    }

    @Override
    public void outputJsonElement(Object obj) throws IOException {
        // add Joda-Time types and enum / enumset types
        if (obj instanceof Enum) {
            // distinguish Tokenizable
            if (obj instanceof TokenizableEnum) {
                outputTokenizableEnum((TokenizableEnum)obj);
            } else {
                outputNonTokenizableEnum((Enum<?>)obj);
            }
            return;
        }
        if (obj instanceof AbstractXEnumBase<?>) {
            outputUnicodeNoControls(((TokenizableEnum)obj).getToken());
            return;
        }
        if (obj instanceof EnumSetMarker) {
            outputEnumSet(obj);
            return;
        }
        if (obj instanceof Instant) {
            outputInstant((Instant)obj);
            return;
        }
        if (obj instanceof LocalDate) {
            appendable.append('"');
            appendLocalDate((LocalDate)obj);
            appendable.append('"');
            return;
        }
        if (obj instanceof LocalTime) {
            appendable.append('"');
            appendLocalTime((LocalTime)obj);
            appendable.append('"');
            return;
        }
        if (obj instanceof LocalDateTime) {
            final LocalDateTime ldt = (LocalDateTime)obj;
            appendable.append('"');
            appendLocalDate(ldt.toLocalDate());
            appendable.append('T');
            appendLocalTime(ldt.toLocalTime());
            appendable.append(TIMEZONE_SUFFIX_FOR_LOCAL);
            appendable.append('"');
            return;
        }

        super.outputJsonElement(obj);
    }
}
