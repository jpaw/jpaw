package de.jpaw.fixedpoint.money;

import java.util.List;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.types.Hundreds;

public class FPAmountOperators {
    static public FPAmount operator_plus(FPAmount a, FPAmount b) {
        return a.add(b);
    }
    static public FPAmount operator_plus(FPAmount net, List<Hundreds> tax) {
        return net.netToGross(tax);
    }
    static public FPAmount operator_minus(FPAmount net, List<? extends FixedPointBase<?>> tax) {
        return net.grossToNet(tax);
    }

    static public FPAmount operator_minus(FPAmount a) {
        return a.negate();
    }
    static public FPAmount operator_minus(FPAmount a, FPAmount b) {
        return a.subtract(b);
    }
    static public FPAmount operator_multiply(FPAmount a, int b) {
        return a.multiply(b);
    }
    static public boolean operator_equals(FPAmount a, FPAmount b) {
        return (a == null) ? b == null : a.equals(b);
    }
    static public boolean operator_notEquals(FPAmount a, FPAmount b) {
        return (a == null) ? b != null : !a.equals(b);
    }
}
