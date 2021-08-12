package de.jpaw.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.joda.time.LocalDateTime;

public class LocalDateTimeAdapter
    extends XmlAdapter<String, LocalDateTime>{

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return new LocalDateTime(v);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return v.toString();
    }

}
