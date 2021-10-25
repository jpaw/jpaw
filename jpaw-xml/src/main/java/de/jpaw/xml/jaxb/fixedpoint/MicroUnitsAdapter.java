package de.jpaw.xml.jaxb.fixedpoint;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.fixedpoint.types.MicroUnits;

public class MicroUnitsAdapter extends XmlAdapter<String, MicroUnits> {

    @Override
    public MicroUnits unmarshal(final String v) throws Exception {
        return MicroUnits.valueOf(v);
    }

    @Override
    public String marshal(final MicroUnits v) throws Exception {
        return v.toString();
    }
}
