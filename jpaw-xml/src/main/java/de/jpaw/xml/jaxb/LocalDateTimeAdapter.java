package de.jpaw.xml.jaxb;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;
import de.jpaw.util.ExceptionUtil;
import de.jpaw.util.FormattersAndParsers;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeAdapter.class);

    private static final String  addSuffixTimezone;     // add suffix "Z" (or other) on output (to simulate UTC time zone)
    private static final boolean tolerateSuffixUTC;    // ignore suffix "Z" when parsing
    private static final boolean ignoreFractionalSeconds;    // ignore fractional seconds when parsing
    private static final boolean outputFractionalSeconds;     // output fractional seconds

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        addSuffixTimezone = cfgReader.getProperty("jpaw.xml.LocalDateTime.timezoneSuffix", null);
        tolerateSuffixUTC = cfgReader.getBooleanProperty("jpaw.xml.LocalDateTime.tolerateSuffixUTC", false);
        ignoreFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.LocalDateTime.ignoreFractionalSeconds", false);
        outputFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.LocalDateTime.outputFractionalSeconds", true);

        LOGGER.info(
          "jpaw.xml.LocalDateTime configuration is: addSuffixTimezone {}, tolerateSuffixUTC {}, ignoreFractionalSeconds {}, outputFractionalSeconds {}",
          addSuffixTimezone, tolerateSuffixUTC, ignoreFractionalSeconds, outputFractionalSeconds);
    }

    @Override
    public String marshal(final LocalDateTime dateTime) {
        try {
            final StringBuilder sb = new StringBuilder(30);
            FormattersAndParsers.appendLocalDateTime(sb, dateTime, outputFractionalSeconds, addSuffixTimezone);
            return sb.toString();
        } catch (final Exception e) {
            LOGGER.error("This should not happen - {} on StringBuilder: {}", e.getClass().getSimpleName(), ExceptionUtil.causeChain(e));
            return null;  // cannot happen (IOException on StringBuilder)
        }
    }

    @Override
    public LocalDateTime unmarshal(final String dateTime) {
        return FormattersAndParsers.parseLocalDateTime(dateTime, ignoreFractionalSeconds, tolerateSuffixUTC);
    }
}
