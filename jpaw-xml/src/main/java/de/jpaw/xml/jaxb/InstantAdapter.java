package de.jpaw.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    public static boolean outputFractionalSeconds   = true;     // output fractional seconds
    public static boolean ignoreFractionalSeconds   = false;    // ignore fractional seconds when parsing

    @Override
    public Instant unmarshal(String v) throws Exception {
        final Instant fullPrecision = Instant.parse(v);
        if (ignoreFractionalSeconds && fullPrecision.getNano() != 0) {
            return Instant.ofEpochSecond(fullPrecision.getEpochSecond());
        }
        return fullPrecision;
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
