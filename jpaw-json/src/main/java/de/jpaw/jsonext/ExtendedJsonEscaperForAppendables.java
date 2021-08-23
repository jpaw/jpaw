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

    protected String toDay(int [] values) {
        return String.format("%04d-%02d-%02d", values[0], values[1], values[2]);
    }

    protected String toTimeOfDay(int hour, int minute, int second, int millis) {
            final String fracs = (millis == 0) ? "" : String.format(".%03d", millis);
            return String.format("%02d:%02d:%02d%s%s", hour, minute, second, fracs, TIMEZONE_SUFFIX_FOR_LOCAL);
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
        long millis = ((Instant)obj).getNano() / 1000000L;
        if (instantInMillis) {
            appendable.append(Long.toString(millis));
        } else {
            appendable.append(Long.toString(millis / 1000));
            millis %= 1000;
            if (millis > 0)
                appendable.append(String.format(".%03d", millis));
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
            LocalDate ld = (LocalDate)obj;
            outputAscii(String.format("%04d-%02d-%02d", ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth())); 
            return;
        }
        if (obj instanceof LocalTime) {
            LocalTime ld = (LocalTime)obj;
            int millis = ld.getNano() / 1000000;
            outputAscii(toTimeOfDay(ld.getHour(), ld.getMinute(), ld.getSecond(), millis));
            return;
        }
        if (obj instanceof LocalDateTime) {
            LocalDateTime ld = (LocalDateTime)obj;
            int millis = ld.getNano() / 1000000;
            outputAscii(String.format("%04d-%02d-%02dT%s", ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(),
                    toTimeOfDay(ld.getHour(), ld.getMinute(), ld.getSecond(), millis)
            ));
            return;
        }

        super.outputJsonElement(obj);
    }
}
