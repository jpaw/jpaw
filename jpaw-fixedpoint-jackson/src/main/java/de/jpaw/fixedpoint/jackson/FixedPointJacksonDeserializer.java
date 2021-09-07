package de.jpaw.fixedpoint.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.FixedPointFactory;

public class FixedPointJacksonDeserializer<CLASS extends FixedPointBase<CLASS>> extends StdDeserializer<CLASS> {
    private static final long serialVersionUID = -194246924592207982L;

    final private FixedPointFactory<CLASS> factory;

    public FixedPointJacksonDeserializer(FixedPointFactory<CLASS> factory) {
        super(factory.instanceClass());
        this.factory = factory;
    }

    @Override
    public CLASS deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken whatIsThere = p.currentToken();
        if (whatIsThere == null) {
            return null;
        }
        switch (whatIsThere) {
        case VALUE_NULL:
            return null;
        case VALUE_NUMBER_INT:
            return factory.valueOf(p.getLongValue());
        case VALUE_NUMBER_FLOAT:
            return factory.valueOf(p.getDecimalValue());  // not ideal, since it constructs a temporary BigDecimal. Can we access the source string directly?
        default:
            ctxt.reportInputMismatch(factory.instanceClass(), "FixedPointParser: Invalid token " + whatIsThere);
            return null; // does not occur due to exception
        }
    }
}
