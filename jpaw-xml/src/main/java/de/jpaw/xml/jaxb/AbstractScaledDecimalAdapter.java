package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    private final boolean allowRounding;
    private final int scale;

    protected AbstractScaledDecimalAdapter(final int scale, final boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }

    private BigDecimal scaleAndRound(final BigDecimal decimal) {
        return decimal.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
    }

    @Override
    public BigDecimal unmarshal(final String value) throws Exception {
        final BigDecimal decimal = new BigDecimal(value);
        if (decimal.signum() == 0)
            return BigDecimal.ZERO;

        return scaleAndRound(decimal);
    }

    @Override
    public String marshal(final BigDecimal value) throws Exception {
        return scaleAndRound(value).toPlainString();
    }
}
