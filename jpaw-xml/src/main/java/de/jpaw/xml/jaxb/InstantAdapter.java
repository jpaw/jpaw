package de.jpaw.xml.jaxb;

import java.time.Instant;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    public static boolean outputFractionalSeconds   = true;     // output fractional seconds
    public static boolean ignoreFractionalSeconds   = false;    // ignore fractional seconds when parsing
    public static boolean addMissingSuffixUTC       = false;    // auto-add suffix "Z" if missing when parsing

    @Override
    public Instant unmarshal(String v) throws Exception {
        if (addMissingSuffixUTC) {
            if (v.charAt(v.length() - 1) != 'Z') {
                v = v + "Z";
            }
        }
        final Instant fullPrecision = Instant.parse(v);
        if (ignoreFractionalSeconds && fullPrecision.getNano() != 0) {
            return Instant.ofEpochSecond(fullPrecision.getEpochSecond());
        }
        return fullPrecision;
    }

    @Override
    public String marshal(final Instant v) throws Exception {
        if (outputFractionalSeconds || v.getNano() == 0) {
            return v.toString();
        } else {
            return Instant.ofEpochSecond(v.getEpochSecond()).toString();
        }
    }
}
