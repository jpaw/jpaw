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

    private String toDay(int [] values) {
        return String.format("%04d-%02d-%02d", values[0], values[1], values[2]);
    }

    private String toTimeOfDay(int millis) {
        int tmpValue = millis / 60000; // minutes and hours
        int frac = millis % 1000;
        String fracs = (frac == 0) ? "" : String.format(".%03d", frac);
        return String.format("%02d:%02d:%02d%s", tmpValue / 60, tmpValue % 60, millis / 1000, fracs);
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
                int [] values = ((LocalDate)obj).getValues();   // 3 values: year, month, day
                outputAscii(toDay(values));
                return;
            }
            if (obj instanceof LocalTime) {
                outputAscii(toTimeOfDay(((LocalTime)obj).getMillisOfDay()));
                return;
            }
            if (obj instanceof LocalDateTime) {
                int [] values = ((LocalDateTime)obj).getValues();   // 4 values: year, month, day, millis
                outputAscii(toDay(values) + "T" + toTimeOfDay(values[3]) + "Z");
                return;
            }
            throw new RuntimeException("Cannot transform joda readable partial of type " + obj.getClass().getSimpleName() + " to JSON");
        }
        super.outputJsonElement(obj);
    }
}
