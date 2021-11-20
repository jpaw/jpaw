package de.jpaw.jsonext;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
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
import de.jpaw.util.ConfigurationReaderFactory;
import de.jpaw.util.FormattersAndParsers;

/**
 * A specialized JSON escaper which uses specific formats for certain types,
 * and provides convenient hooks for customization.
 *
 */
public class ExtendedJsonEscaperForAppendables extends BaseJsonComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedJsonEscaperForAppendables.class);

    private static final String  addSuffixTimezone;     // add suffix "Z" (or other) on output (to simulate UTC time zone)
    private static final boolean defaultOutputFractionalSeconds;     // output fractional seconds
    private static final boolean alwaysOutputFractionals;     // output fractional seconds even if they are 0

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        addSuffixTimezone = cfgReader.getProperty("jpaw.json.LocalDateTime.timezoneSuffix", null);
        defaultOutputFractionalSeconds = cfgReader.getBooleanProperty("jpaw.json.outputFractionalSeconds", true);
        alwaysOutputFractionals        = cfgReader.getBooleanProperty("jpaw.json.alwaysOutputFractionals", true);

        LOGGER.info("jpaw.json configuration is: addSuffixTimezone {}, outputFractionalSeconds {}, alwaysOutputFractionals {}",
          addSuffixTimezone, defaultOutputFractionalSeconds, alwaysOutputFractionals);
    }

    //    @Deprecated
//    public static boolean defaultInstantInMillis         = false;   // if false: the unit is a second

    // if instantInMillis is true, Instants will be written as integral values in milliseconds, otherwise as second + optional fractional parts
    // see DATE_TIMESTAMPS_AS_NANOSECONDS in https://github.com/FasterXML/jackson-datatype-jsr310 for similar setting
    protected final boolean instantInMillis;
    protected final boolean outputFractionalSeconds;

    public ExtendedJsonEscaperForAppendables(final Appendable appendable) {
        super(appendable);      // default: writeNulls = true, escapeNonAscii = false
        instantInMillis              = false; // defaultInstantInMillis;
        outputFractionalSeconds      = defaultOutputFractionalSeconds;
    }

    public ExtendedJsonEscaperForAppendables(final Appendable appendable, final boolean writeNulls, final boolean escapeNonASCII, final boolean instantInMillis) {
        super(appendable, writeNulls, escapeNonASCII);
        this.instantInMillis         = instantInMillis;
        outputFractionalSeconds      = defaultOutputFractionalSeconds;
    }

    public ExtendedJsonEscaperForAppendables(final Appendable appendable, final boolean writeNulls, final boolean escapeNonASCII, final boolean instantInMillis,
      final boolean outputFractionalSeconds) {
        super(appendable, writeNulls, escapeNonASCII);
        this.instantInMillis         = instantInMillis;
        this.outputFractionalSeconds = outputFractionalSeconds;
    }


    // provided as a hook to allow overriding
    protected void outputEnumSet(final Object obj) throws IOException {
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
    protected void outputTokenizableEnum(final TokenizableEnum obj) throws IOException {
        outputUnicodeNoControls(obj.getToken());
    }

    // provided as a hook to allow overriding
    protected void outputNonTokenizableEnum(final Enum<?> obj) throws IOException {
        outputNumber(obj.ordinal());
    }

    // provided as a hook to allow overriding
    protected void outputInstant(final Instant obj) throws IOException {
        final long seconds = obj.getEpochSecond();
        final int millis = obj.getNano() / 1000000;
        if (instantInMillis) {
            appendable.append(Long.toString(1000L * seconds + millis));
        } else {
            appendable.append(Long.toString(seconds));
            if (outputFractionalSeconds) {
                if (millis > 0) {
                    FormattersAndParsers.appendMilliseconds(appendable, millis);
                }
            }
        }
    }

    @Override
    public void outputJsonElement(final Object obj) throws IOException {
        // add Java 8 date/time types and enum / enumset types
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
            FormattersAndParsers.appendLocalDate(appendable, (LocalDate)obj);
            appendable.append('"');
            return;
        }
        if (obj instanceof LocalTime) {
            appendable.append('"');
            FormattersAndParsers.appendLocalTime(appendable, (LocalTime)obj, outputFractionalSeconds, alwaysOutputFractionals);
            appendable.append('"');
            return;
        }
        if (obj instanceof LocalDateTime) {
            final LocalDateTime ldt = (LocalDateTime)obj;
            appendable.append('"');
            FormattersAndParsers.appendLocalDateTime(appendable, ldt, outputFractionalSeconds, alwaysOutputFractionals, addSuffixTimezone);
            appendable.append('"');
            return;
        }

        super.outputJsonElement(obj);
    }
}
