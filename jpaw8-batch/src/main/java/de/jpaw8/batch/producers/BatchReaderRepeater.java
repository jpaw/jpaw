package de.jpaw8.batch.producers;

import java.util.function.ObjIntConsumer;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchReader;

/** Batch reader for testing. This one repeats the provided input a specified number of times,
 *  and optionally waits between data production. */
public class BatchReaderRepeater<E> implements BatchReader<E> {
    static private final String OPTION = "num";
    private final E objectToRepeat;
    private int numRepeats = 1;
    private final CmdlineParserContext ctx;

    public BatchReaderRepeater(E objectToRepeat, int num) {
        this.objectToRepeat = objectToRepeat;
        this.numRepeats = num;
        ctx = null;
    }

    public BatchReaderRepeater(E objectToRepeat) {
        this.objectToRepeat = objectToRepeat;
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption(OPTION, JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 'n', "number of repetitions");
    }

    @Override
    public void produceTo(ObjIntConsumer<? super E> whereToPut) throws Exception {
        if (ctx != null)
            numRepeats = ctx.getInt(OPTION);
        for (int i = 1; i <= numRepeats; ++i) {
            whereToPut.accept(objectToRepeat, i);
        }
    }
}
