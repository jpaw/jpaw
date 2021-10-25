package de.jpaw.xml.jaxb;

import java.time.LocalTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(final String v) throws Exception {
        return LocalTime.parse(v);
    }

    @Override
    public String marshal(final LocalTime v) throws Exception {
        return v.toString();
    }
}
