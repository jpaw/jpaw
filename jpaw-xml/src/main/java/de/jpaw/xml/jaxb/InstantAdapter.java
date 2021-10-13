package de.jpaw.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    public static boolean outputFractionalSeconds   = true;     // output fractional seconds

    @Override
    public Instant unmarshal(String v) throws Exception {
        return Instant.parse(v);
    }

    @Override
    public String marshal(Instant v) throws Exception {
        if (outputFractionalSeconds || v.getNano() == 0) {
            return v.toString();
        } else {
            return Instant.ofEpochSecond(v.getEpochSecond()).toString();
        }
    }
}
