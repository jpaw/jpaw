package de.jpaw.xml.jaxb.fixedpoint;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.fixedpoint.types.MicroUnits;

public class MicroUnitsAdapter extends XmlAdapter<String, MicroUnits> {

    @Override
    public MicroUnits unmarshal(String v) throws Exception {
        return MicroUnits.valueOf(v);
    }

    @Override
    public String marshal(MicroUnits v) throws Exception {
        return v.toString();
    }
}
