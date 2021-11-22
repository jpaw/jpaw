package de.jpaw.xml.jaxb;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;
import de.jpaw.util.FormattersAndParsers;

public class LocalDateAdapter  extends XmlAdapter<String, LocalDate> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateAdapter.class);

    private static final boolean tolerateExtraTime;         // accept when also a time is sent (ignore it)

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        tolerateExtraTime = cfgReader.getBooleanProperty("jpaw.xml.LocalDate.tolerateExtraTime", false);

        LOGGER.info("jpaw.xml.LocalDate configuration is: tolerateExtraTime {}", tolerateExtraTime);
    }

    @Override
    public LocalDate unmarshal(final String v) throws Exception {
        if (v.length() > FormattersAndParsers.LENGTH_OF_ISO_DATE && v.charAt(FormattersAndParsers.LENGTH_OF_ISO_DATE) == 'T') {
            return LocalDate.parse(v.substring(0, FormattersAndParsers.LENGTH_OF_ISO_DATE));
        } else {
            return LocalDate.parse(v);
        }
    }

    @Override
    public String marshal(final LocalDate v) throws Exception {
        return v.toString();
    }
}
