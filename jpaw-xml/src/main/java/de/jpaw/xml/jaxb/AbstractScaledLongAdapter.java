package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledLongAdapter extends XmlAdapter<BigDecimal, Long> {
    private static final Long ZERO = Long.valueOf(0L);
    private final boolean allowRounding;
    private final int scale;

    protected AbstractScaledLongAdapter(final int scale, final boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }

    @Override
    public Long unmarshal(final BigDecimal v) throws Exception {
        if (v.signum() == 0)
            return ZERO;  // always valid
        final BigDecimal vv = v.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        return vv.unscaledValue().longValue();
    }

    @Override
    public BigDecimal marshal(final Long v) throws Exception {
        return BigDecimal.valueOf(v.longValue(), scale);
    }
}
