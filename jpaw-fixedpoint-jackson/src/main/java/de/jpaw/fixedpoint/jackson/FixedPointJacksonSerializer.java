package de.jpaw.fixedpoint.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import de.jpaw.fixedpoint.FixedPointBase;

public class FixedPointJacksonSerializer<CLASS extends FixedPointBase<CLASS>> extends StdSerializer<CLASS> {
    private static final long serialVersionUID = -194246924592207968L;

    public FixedPointJacksonSerializer() {
        this(null);
    }

    public FixedPointJacksonSerializer(Class<CLASS> t) {
        super(t);
    }

    @Override
    public void serialize(CLASS value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.toString());
    }
}
