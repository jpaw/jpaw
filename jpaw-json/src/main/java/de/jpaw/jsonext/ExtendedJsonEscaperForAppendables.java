package de.jpaw.jsonext;

import java.io.IOException;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;

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

    protected String toTimeOfDay(int millis) {
        int tmpValue = millis / 60000; // minutes and hours
        int frac = millis % 1000;
        String fracs = (frac == 0) ? "" : String.format(".%03d", frac);
        return String.format("%02d:%02d:%02d%s", tmpValue / 60, tmpValue % 60, millis / 1000, fracs);
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
        long millis = ((Instant)obj).getMillis();
        if (instantInMillis) {
            appendable.append(Long.toString(millis));
        } else {
            appendable.append(Long.toString(millis / 1000));
            millis %= 1000;
            if (millis > 0)
                appendable.append(String.format(".%03d", millis));
        }
    }

    // provided as a hook to allow overriding
    protected void outputTemporal(Object obj) throws IOException {
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
        if (obj instanceof ReadablePartial) {
            outputTemporal(obj);
            return;
        }
        super.outputJsonElement(obj);
    }
}
