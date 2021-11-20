package de.jpaw.xml.jaxb;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;
import de.jpaw.util.FormattersAndParsers;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstantAdapter.class);

    private static final boolean outputFractionalSeconds;   // output fractional seconds
    private static final boolean ignoreFractionalSeconds;   // ignore fractional seconds when parsing
    private static final boolean addMissingSuffixUTC;       // auto-add suffix "Z" if missing when parsing
    private static final boolean alwaysOutputFractionals;     // output fractional seconds even if they are 0


    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        addMissingSuffixUTC     = cfgReader.getBooleanProperty("jpaw.xml.Instant.addMissingSuffixUTC",     false);
        ignoreFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.Instant.ignoreFractionalSeconds", false);
        outputFractionalSeconds = cfgReader.getBooleanProperty("jpaw.xml.Instant.outputFractionalSeconds", true);
        alwaysOutputFractionals = cfgReader.getBooleanProperty("jpaw.xml.Instant.alwaysOutputFractionals", true);

        LOGGER.info("jpaw.xml.Instant configuration is: addMissingSuffixUTC {}, ignoreFractionalSeconds {},"
          + " outputFractionalSeconds {}, alwaysOutputFractionals {}",
          addMissingSuffixUTC, ignoreFractionalSeconds, outputFractionalSeconds, alwaysOutputFractionals);
    }

    @Override
    public Instant unmarshal(String v) throws Exception {
        if (addMissingSuffixUTC) {
            if (v.charAt(v.length() - 1) != 'Z') {
                v = v + "Z";
            }
        }
        final Instant fullPrecision = Instant.parse(v);
        if (ignoreFractionalSeconds && fullPrecision.getNano() != 0) {
            return Instant.ofEpochSecond(fullPrecision.getEpochSecond());
        }
        return fullPrecision;
    }

    @Override
    public String marshal(final Instant v) throws Exception {
        final StringBuilder sb = new StringBuilder(30);
        FormattersAndParsers.appendLocalDateTime(sb, LocalDateTime.ofInstant(v, ZoneOffset.UTC), outputFractionalSeconds, alwaysOutputFractionals, "Z");
        return sb.toString();
    }
}
