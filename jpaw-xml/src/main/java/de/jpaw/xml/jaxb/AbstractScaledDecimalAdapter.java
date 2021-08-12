package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    private final boolean allowRounding;
    private final int scale;

    protected AbstractScaledDecimalAdapter(int scale, boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }

    private BigDecimal scaleAndRound(BigDecimal decimal) {
        return decimal.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
    }

    @Override
    public BigDecimal unmarshal(String value) throws Exception {
        BigDecimal decimal = new BigDecimal(value);
        if (decimal.signum() == 0)
            return BigDecimal.ZERO;

        return scaleAndRound(decimal);
    }

    @Override
    public String marshal(BigDecimal value) throws Exception {
        return scaleAndRound(value).toPlainString();
    }
}
