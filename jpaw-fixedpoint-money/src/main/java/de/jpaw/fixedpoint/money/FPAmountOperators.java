package de.jpaw.fixedpoint.money;

import java.util.List;

import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.fixedpoint.types.Hundreds;

public final class FPAmountOperators {
    private FPAmountOperators() { }

    public static FPAmount operator_plus(FPAmount a, FPAmount b) {
        return a.add(b);
    }
    public static FPAmount operator_plus(FPAmount net, List<Hundreds> tax) {
        return net.netToGross(tax);
    }
    public static FPAmount operator_minus(FPAmount net, List<? extends FixedPointBase<?>> tax) {
        return net.grossToNet(tax);
    }

    public static FPAmount operator_minus(FPAmount a) {
        return a.negate();
    }
    public static FPAmount operator_minus(FPAmount a, FPAmount b) {
        return a.subtract(b);
    }
    public static FPAmount operator_multiply(FPAmount a, int b) {
        return a.multiply(b);
    }
    public static boolean operator_equals(FPAmount a, FPAmount b) {
        return (a == null) ? b == null : a.equals(b);
    }
    public static boolean operator_notEquals(FPAmount a, FPAmount b) {
        return (a == null) ? b != null : !a.equals(b);
    }
}
