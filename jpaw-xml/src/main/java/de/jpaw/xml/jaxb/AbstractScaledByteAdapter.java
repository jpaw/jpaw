package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledByteAdapter extends XmlAdapter<BigDecimal, Byte> {
    private static final Byte ZERO = Byte.valueOf((byte) 0);
    private final boolean allowRounding;
    private final int scale;

    protected AbstractScaledByteAdapter(final int scale, final boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }

    @Override
    public Byte unmarshal(final BigDecimal v) throws Exception {
        if (v.signum() == 0)
            return ZERO;  // always valid
        final BigDecimal vv = v.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        return vv.unscaledValue().byteValue();
    }

    @Override
    public BigDecimal marshal(final Byte v) throws Exception {
        return BigDecimal.valueOf(v.byteValue(), scale);
    }
}
