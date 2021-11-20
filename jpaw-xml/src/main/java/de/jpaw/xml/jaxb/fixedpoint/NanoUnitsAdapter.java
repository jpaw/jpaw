package de.jpaw.xml.jaxb.fixedpoint;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.fixedpoint.types.NanoUnits;
import de.jpaw.util.ConfigurationReaderFactory;

public class NanoUnitsAdapter extends XmlAdapter<String, NanoUnits> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NanoUnitsAdapter.class);

    private static final int minFractionalDigits;      // the minimum number of fractional digits to output

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        minFractionalDigits = cfgReader.getIntProperty("jpaw.xml.NanoUnits.minFractionalDigits", 3);

        LOGGER.info("jpaw.xml.NanoUnits configuration is: minFractionalDigits {}", minFractionalDigits);
    }

    @Override
    public NanoUnits unmarshal(final String v) throws Exception {
        return NanoUnits.valueOf(v);
    }

    @Override
    public String marshal(final NanoUnits v) throws Exception {
        return v.toString(minFractionalDigits);
    }
}
