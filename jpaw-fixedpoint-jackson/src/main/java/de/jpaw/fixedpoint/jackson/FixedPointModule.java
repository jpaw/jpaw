package de.jpaw.fixedpoint.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import de.jpaw.fixedpoint.factories.MicroUnitsFactory;
import de.jpaw.fixedpoint.factories.MilliUnitsFactory;
import de.jpaw.fixedpoint.factories.NanoUnitsFactory;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.NanoUnits;

public class FixedPointModule extends SimpleModule {
    private static final long serialVersionUID = -5852568871709312856L;

    public FixedPointModule() {
        super();

        this.addSerializer(MilliUnits.class, new FixedPointJacksonSerializer<MilliUnits>(MilliUnits.class));
        this.addSerializer(MicroUnits.class, new FixedPointJacksonSerializer<MicroUnits>(MicroUnits.class));
        this.addSerializer(NanoUnits.class,  new FixedPointJacksonSerializer<NanoUnits> (NanoUnits.class));

        this.addDeserializer(MilliUnits.class, new FixedPointJacksonDeserializer<MilliUnits>(new MilliUnitsFactory()));
        this.addDeserializer(MicroUnits.class, new FixedPointJacksonDeserializer<MicroUnits>(new MicroUnitsFactory()));
        this.addDeserializer(NanoUnits.class,  new FixedPointJacksonDeserializer<NanoUnits> (new NanoUnitsFactory()));
    }
}
