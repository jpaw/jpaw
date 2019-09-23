package de.jpaw8.batch.producers.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchReader;

abstract public class BatchReaderFile<Q> implements BatchReader<Q> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchReaderFile.class);
    private static final int BUFFER_SIZE = 65536;       // GZIP Buffer size (tunable constant for performance)
    private boolean useGzip = false;
    private boolean useZip = false;
    private String filename = null;
    private InputStream rawStream = null;
    protected InputStream uncompressedStream = null;    // the effective input. Subclasses can add buffering and decoding
    protected boolean isBuffered = false;               // information if this stream is buffered already, to avoid duplicate buffers
    private final CmdlineParserContext ctx;

    public BatchReaderFile() {
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("in", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, 'i', "input filename (extensions .gz and .zip are understood)");
        ctx.addSwitch("inzip", JSAP.NO_SHORTFLAG, "unzip input file on the fly");
        ctx.addSwitch("ingzip", JSAP.NO_SHORTFLAG, "gunzip input file on the fly");
    }

    @Override
    public void open() throws Exception {
        useGzip = ctx.getBoolean("ingzip");
        useZip = ctx.getBoolean("inzip");
        filename = ctx.getString("in");
        if (filename != null) {
            if (filename.endsWith(".gz") || filename.endsWith(".GZ"))
                useGzip = true;
            if (filename.endsWith(".zip") || filename.endsWith(".ZIP"))
                useZip = true;
        }
        // plausi check: cannot do both gzip and zip
        if (useGzip && useZip) {
            LOG.error("Cannot use both gzip and zip compression for input at the same time");
            // fatal error, terminate
            System.exit(1);
        }

        // command line parsed, now open the input (and check for existence of file)
        // if the file does not exist, we terminate without doing anything
        if (filename == null) {
            filename = "(stdin)";   // have something readable
            rawStream = System.in;
        } else {
            try {
                rawStream = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                LOG.error("Cannot open file {} for input", filename);
                // fatal error, terminate
                System.exit(1);
            }
        }
        if (useGzip) {
            uncompressedStream = new GZIPInputStream(rawStream, BUFFER_SIZE);
            isBuffered = true;
        } else if (useZip) {
            ZipInputStream zipInput = new ZipInputStream(rawStream);
            zipInput.getNextEntry();
            uncompressedStream = zipInput;
        } else {
            uncompressedStream = rawStream;
        }
    }

    @Override
    public void close() throws Exception {
        uncompressedStream.close();
    }
}
