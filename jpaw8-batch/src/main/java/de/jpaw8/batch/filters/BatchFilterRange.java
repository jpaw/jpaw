package de.jpaw8.batch.filters;

import java.util.function.IntPredicate;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineCallback;
import de.jpaw.cmdline.CmdlineParserContext;

/** Misuse of the filter to implement skipping records or limiting records. */
public class BatchFilterRange implements IntPredicate, CmdlineCallback {
    static private final String OPTION_MIN = "min";
    static private final String OPTION_MAX = "max";
    protected int recordNoMin = 1;
    protected int recordNoMax = Integer.MAX_VALUE;

    /** hardcoded range. */
    public BatchFilterRange(int recordNoMin, int recordNoMax) {
        this.recordNoMin = recordNoMin;
        this.recordNoMax = recordNoMax;
    }

    /** Range specified by command line. */
    public BatchFilterRange() {
        CmdlineParserContext.getContext()
            .addFlaggedOption(OPTION_MIN, JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "smallest record number to pass")
            .addFlaggedOption(OPTION_MAX, JSAP.INTEGER_PARSER, "2000000000", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "biggest record number to pass")
            .registerCallback(this);
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        recordNoMin = ctx.getInt(OPTION_MIN);
        recordNoMax = ctx.getInt(OPTION_MAX);
    }

    @Override
    public boolean test(int i) {
        return recordNoMin <= i && i <= recordNoMax;
    }

}
