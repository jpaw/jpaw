package de.jpaw.xml.jaxb.fixedpoint;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.fixedpoint.types.NanoUnits;

public class NanoUnitsAdapter extends XmlAdapter<String, NanoUnits> {

    @Override
    public NanoUnits unmarshal(final String v) throws Exception {
        return NanoUnits.valueOf(v);
    }

    @Override
    public String marshal(final NanoUnits v) throws Exception {
        return v.toString();
    }
}
