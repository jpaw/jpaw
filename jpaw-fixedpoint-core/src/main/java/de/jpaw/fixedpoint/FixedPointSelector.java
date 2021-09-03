package de.jpaw.fixedpoint;

import de.jpaw.fixedpoint.types.FemtoUnits;
import de.jpaw.fixedpoint.types.Hundreds;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.NanoUnits;
import de.jpaw.fixedpoint.types.PicoUnits;
import de.jpaw.fixedpoint.types.Tenths;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.fixedpoint.types.VariableUnits;

public class FixedPointSelector {
    public static FixedPointBase<?> getZeroForScale(int scale) {
        switch (VariableUnits.scaleCheck(scale)) {
        case  0: return Units.ZERO;
        case  1: return Tenths.ZERO;
        case  2: return Hundreds.ZERO;
        case  3: return MilliUnits.ZERO;
        case  6: return MicroUnits.ZERO;
        case  9: return NanoUnits.ZERO;
        case 12: return PicoUnits.ZERO;
        case 15: return FemtoUnits.ZERO;
        default: return VariableUnits.valueOf(0, scale);
        }
    }
}
