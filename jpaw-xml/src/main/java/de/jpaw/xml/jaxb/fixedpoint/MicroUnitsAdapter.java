package de.jpaw.xml.jaxb.fixedpoint;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.util.ConfigurationReaderFactory;

public class MicroUnitsAdapter extends XmlAdapter<String, MicroUnits> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MicroUnitsAdapter.class);

    private static final int minFractionalDigits;      // the minimum number of fractional digits to output

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        minFractionalDigits = cfgReader.getIntProperty("jpaw.xml.MicroUnits.minFractionalDigits", 2);

        LOGGER.info("jpaw.xml.MicroUnits configuration is: minFractionalDigits {}", minFractionalDigits);
    }

    @Override
    public MicroUnits unmarshal(final String v) throws Exception {
        return MicroUnits.valueOf(v);
    }

    @Override
    public String marshal(final MicroUnits v) throws Exception {
        return v.toString(minFractionalDigits);
    }
}
