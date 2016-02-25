package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledDecimalAdapter;

public class ScaledDecimalAdapter2Round extends AbstractScaledDecimalAdapter {

    public ScaledDecimalAdapter2Round() {
        super(2, true);
    }
}
