package de.jpaw.jsonext;

import java.io.IOException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

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

public class ExtendedJsonEscaperForAppendables extends BaseJsonComposer {

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

    private String toTimeOfDay(int hour, int minute, int second, int millis) {
        final String fracs = (millis == 0) ? "" : String.format(".%03d", millis);
        return String.format("%02d:%02d:%02d%s", hour, minute, second, fracs);
    }

    @Override
    public void outputJsonElement(Object obj) throws IOException {
        // add Joda-Time types and enum types
        if (obj instanceof Enum) {
            // distinguish Tokenizable
            if (obj instanceof TokenizableEnum) {
                outputUnicodeNoControls(((TokenizableEnum)obj).getToken());
            } else {
                outputNumber(((Enum<?>)obj).ordinal());
            }
            return;
        }
        if (obj instanceof AbstractXEnumBase<?>) {
            outputUnicodeNoControls(((TokenizableEnum)obj).getToken());
            return;
        }
        if (obj instanceof EnumSetMarker) {
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
            return;
        }
        if (obj instanceof Temporal) {
            if (obj instanceof Instant) {
                Instant inst = (Instant)obj;
                int millis = inst.getNano() / 1000000;
                if (instantInMillis) {
                    appendable.append(Long.toString(millis + 1000L * inst.getEpochSecond()));
                } else {
                    appendable.append(Long.toString(inst.getEpochSecond()));
                    if (millis > 0)
                        appendable.append(String.format(".%03d", millis));
                }
                return;
            }
            if (obj instanceof LocalDate) {
                LocalDate ld = (LocalDate)obj;
                outputAscii(String.format("%04d-%02d-%02d", ld.getYear(), ld.getMonth(), ld.getDayOfMonth())); 
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
                outputAscii(String.format("%04d-%02d-%02dT%s", ld.getYear(), ld.getMonth(), ld.getDayOfMonth(),
                        toTimeOfDay(ld.getHour(), ld.getMinute(), ld.getSecond(), millis)
                ));       // no appended "Z" any more - that would be ZonedDateTime. See http://javarevisited.blogspot.com/2015/03/20-examples-of-date-and-time-api-from-Java8.html
                return;
            }
            throw new RuntimeException("Cannot transform joda readable partial of type " + obj.getClass().getSimpleName() + " to JSON");
        }
        super.outputJsonElement(obj);
    }
}
