package de.jpaw.xml.jaxb;

import java.time.LocalTime;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;
import de.jpaw.util.ExceptionUtil;
import de.jpaw.util.FormattersAndParsers;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalTimeAdapter.class);

    private static final boolean ignoreFractionalSeconds;    // ignore fractional seconds when parsing
    private static final boolean outputFractionalSeconds;     // output fractional seconds
    private static final boolean alwaysOutputFractionals;     // output fractional seconds even if they are 0

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        ignoreFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.LocalTime.ignoreFractionalSeconds", false);
        outputFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.LocalTime.outputFractionalSeconds", true);
        alwaysOutputFractionals = cfgReader.getBooleanProperty("jpaw.xml.LocalTime.alwaysOutputFractionals", true);

        LOGGER.info("jpaw.xml.LocalTime configuration is: ignoreFractionalSeconds {}, outputFractionalSeconds {}, alwaysOutputFractionals {}",
          ignoreFractionalSeconds, outputFractionalSeconds, alwaysOutputFractionals);
    }

    @Override
    public LocalTime unmarshal(final String v) throws Exception {
        return FormattersAndParsers.parseLocalTime(v, ignoreFractionalSeconds);
    }

    @Override
    public String marshal(final LocalTime v) throws Exception {
        try {
            final StringBuilder sb = new StringBuilder(30);
            FormattersAndParsers.appendLocalTime(sb, v, outputFractionalSeconds, alwaysOutputFractionals);
            return sb.toString();
        } catch (final Exception e) {
            LOGGER.error("This should not happen - {} on StringBuilder: {}", e.getClass().getSimpleName(), ExceptionUtil.causeChain(e));
            return null;  // cannot happen (IOException on StringBuilder)
        }
    }
}
