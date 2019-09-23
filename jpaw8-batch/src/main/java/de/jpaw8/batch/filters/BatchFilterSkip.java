package de.jpaw8.batch.filters;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;

/** Skips the first n records of a batch. (for example to discard header lines) */
public class BatchFilterSkip extends BatchFilterRange {
    static private final String OPTION = "skip";

    /** hardcoded range. */
    public BatchFilterSkip(int lines) {
        super(lines + 1, Integer.MAX_VALUE);
    }

    /** Range specified by command line. */
    public BatchFilterSkip() {
        CmdlineParserContext.getContext()
            .addFlaggedOption(OPTION, JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "number of records to skip")
            .registerCallback(this);
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        recordNoMin = ctx.getInt(OPTION) + 1;
    }

}
