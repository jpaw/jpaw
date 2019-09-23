package de.jpaw8.batch.consumers.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.impl.BatchCharsetUtil;

/** Text file output class.
 * Provides the method write(String);
 *
 */
abstract public class BatchWriterTextFileAbstract<Q> extends BatchWriterFile<Q> {
    protected Charset encoding = StandardCharsets.UTF_8;
    protected BufferedWriter bufferedWriter = null;
    private final String header;
    private final String footer;
    private final CmdlineParserContext ctx;

    public BatchWriterTextFileAbstract(String header, String footer) {
        this.header = header;
        this.footer = footer;
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("outcs", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "output encoding (default is UTF-8, LIST to get a list of available character sets)");
    }

    public BatchWriterTextFileAbstract() {
        this(null, null);
    }


    @Override
    public void open() throws Exception {
        super.open();

        // encoding has been clarified. Now technically everything is fine, get the actual file. That will provide the stream uncompressedStream in the superclass
        encoding = BatchCharsetUtil.charsetFromStringWithHelp(ctx.getString("outcs"));
        // provide the buffering and charset decoding on top...
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(uncompressedStream, encoding));

        if (header != null)
            bufferedWriter.write(header);
    }

    public void write(String line) {
        try {
            bufferedWriter.write(line);
        } catch (IOException e) {
            throw new RuntimeException(e);      // sneaky throw
        }
    }

    @Override
    public void close() throws Exception {
        if (footer != null)
            bufferedWriter.write(footer);
        bufferedWriter.flush();
        bufferedWriter.close();
        super.close();  // redundant / duplicate close call?
    }

}
