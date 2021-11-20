package de.jpaw.fixedpoint.jackson;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ConfigurationReaderFactory;

public class FixedPointJacksonSerializer<CLASS extends FixedPointBase<CLASS>> extends StdSerializer<CLASS> {
    private static final long serialVersionUID = -194246924592207968L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedPointJacksonSerializer.class);

    private static final int minFractionalDigits;      // the minimum number of fractional digits to output

    static {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        minFractionalDigits = cfgReader.getIntProperty("jpaw.json.FixedPoint.minFractionalDigits", 2);

        LOGGER.info("jpaw.json.FixedPoint configuration is: minFractionalDigits {}", minFractionalDigits);
    }

    public FixedPointJacksonSerializer() {
        this(null);
    }

    public FixedPointJacksonSerializer(Class<CLASS> t) {
        super(t);
    }

    @Override
    public void serialize(CLASS value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.toString(minFractionalDigits));
    }
}
