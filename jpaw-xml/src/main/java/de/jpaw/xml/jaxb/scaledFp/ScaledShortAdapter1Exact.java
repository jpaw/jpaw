package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 1 fractional digits. */
public class ScaledShortAdapter1Exact extends AbstractScaledShortAdapter {

    public ScaledShortAdapter1Exact() {
        super(1, false);
    }
}
