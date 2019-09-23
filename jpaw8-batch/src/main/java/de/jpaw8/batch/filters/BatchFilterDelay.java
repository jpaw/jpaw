package de.jpaw8.batch.filters;

import java.util.function.IntPredicate;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineCallback;
import de.jpaw.cmdline.CmdlineParserContext;

/** Misuse of the filter to implement a delay. */
public class BatchFilterDelay implements IntPredicate, CmdlineCallback {
    static private final String OPTION = "delay";
    final String prefix;
    int delay = 0;

    /** hardcoded delay. */
    public BatchFilterDelay(int milliseconds) {
        delay = milliseconds >= 0 ? milliseconds : 0;
        prefix = null;
    }

    /** Delay specified by command line. */
    public BatchFilterDelay(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
        CmdlineParserContext.getContext()
            .addFlaggedOption(prefix + OPTION, JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "number of ms delay between repetitions")
            .registerCallback(this);
    }

    /** Delay specified by command line, no prefix. (Can I have parameter default values, some time?) */
    public BatchFilterDelay() {
        this(null);
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        delay = ctx.getInt(prefix + OPTION);
    }

    @Override
    public boolean test(int i) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return true;
    }

}
