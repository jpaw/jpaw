package de.jpaw.fixedpoint.jackson.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jpaw.fixedpoint.jackson.FixedPointModule;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;

public class FixedPointJacksonTest {
    static class JsonTest {
        public boolean bool;
        public MicroUnits num;
        public MilliUnits num2;
    }
    
    static final String DATA_AS_JSON = "{\"bool\":true,\"num\":3.140000,\"num2\":-3.140}"; 

    private ObjectMapper createMapper() {
        final ObjectMapper om = new ObjectMapper();
        om.registerModule(new FixedPointModule());
        return om;
    }

    @Test
    public void runSerialization() throws Exception {
        final JsonTest srcData = new JsonTest();
        srcData.bool = true;
        srcData.num  = MicroUnits.of(3140000);
        srcData.num2 = MilliUnits.of(-3140);

        final ObjectMapper om = createMapper();
        om.registerModule(new FixedPointModule());
        String srcAsString = om.writeValueAsString(srcData);

        Assertions.assertEquals(DATA_AS_JSON, srcAsString, "Serialized form of srcData");
    }

    @Test
    public void runDeserialization() throws Exception {
        
        final ObjectMapper om = createMapper();
        final JsonTest dstData = om.readValue(DATA_AS_JSON, JsonTest.class);

        Assertions.assertEquals(true, dstData.bool, "match of boolean");
        Assertions.assertEquals(MicroUnits.of(3140000), dstData.num, "match of MicroUnits");
        Assertions.assertEquals(MilliUnits.of(-3140), dstData.num2, "match of MilliUnits");
    }
}
