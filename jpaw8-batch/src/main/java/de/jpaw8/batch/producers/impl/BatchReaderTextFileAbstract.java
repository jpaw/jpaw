package de.jpaw8.batch.producers.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.impl.BatchCharsetUtil;

/** Text file input class.
 * Provides the method String getNextLine();
 *
 */
abstract public class BatchReaderTextFileAbstract<Q> extends BatchReaderFile<Q> {
    private Charset encoding = StandardCharsets.UTF_8;
    private BufferedReader bufferedReader = null;  // the buffered and decoded input.
    private final CmdlineParserContext ctx;

    public BatchReaderTextFileAbstract() {
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("incs", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "input encoding (default is UTF-8, help to get a list of available character sets)");
    }


    @Override
    public void open() throws Exception {
        super.open();
        encoding = BatchCharsetUtil.charsetFromStringWithHelp(ctx.getString("incs"));

        // provide the buffering and charset decoding on top... (need buffering due to readLine() even if parent does buffering already)
        bufferedReader = new BufferedReader(new InputStreamReader(uncompressedStream, encoding));
    }

    public String getNext() throws IOException, InterruptedException {
        return bufferedReader.readLine();
    }
}
