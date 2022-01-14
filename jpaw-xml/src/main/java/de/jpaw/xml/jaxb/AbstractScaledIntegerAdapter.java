package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledIntegerAdapter extends XmlAdapter<BigDecimal, Integer> {
    private static final Integer ZERO = Integer.valueOf(0);
    private final boolean allowRounding;
    private final int scale;

    protected AbstractScaledIntegerAdapter(final int scale, final boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }

    @Override
    public Integer unmarshal(final BigDecimal v) throws Exception {
        if (v.signum() == 0)
            return ZERO;  // always valid
        final BigDecimal vv = v.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        return vv.unscaledValue().intValue();
    }

    @Override
    public BigDecimal marshal(final Integer v) throws Exception {
        return BigDecimal.valueOf(v.intValue(), scale);
    }
}
