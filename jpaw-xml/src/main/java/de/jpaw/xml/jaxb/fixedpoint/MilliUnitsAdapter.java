package de.jpaw.xml.jaxb.fixedpoint;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.util.ConfigurationReaderFactory;

public class MilliUnitsAdapter extends XmlAdapter<String, MilliUnits> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MilliUnitsAdapter.class);

    private static final int minFractionalDigits;      // the minimum number of fractional digits to output

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        minFractionalDigits = cfgReader.getIntProperty("jpaw.xml.MilliUnits.minFractionalDigits", 1);

        LOGGER.info("jpaw.xml.MilliUnits configuration is: minFractionalDigits {}", minFractionalDigits);
    }

    @Override
    public MilliUnits unmarshal(final String v) throws Exception {
        return MilliUnits.valueOf(v);
    }

    @Override
    public String marshal(final MilliUnits v) throws Exception {
        return v.toString(minFractionalDigits);
    }
}
