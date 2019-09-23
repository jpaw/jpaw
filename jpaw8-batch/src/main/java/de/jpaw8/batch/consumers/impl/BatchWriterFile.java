package de.jpaw8.batch.consumers.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchWriter;

// generic parameter only used in superclasses
abstract public class BatchWriterFile<Q> implements BatchWriter<Q> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchWriterFile.class);
    private boolean useGzip = false;
    private boolean useZip = false;
    private String filename = null;
    private OutputStream rawStream = null;
    protected OutputStream uncompressedStream = null;  // the effective input. Subclasses can add buffering and decoding
    private final CmdlineParserContext ctx;

    public BatchWriterFile() {
        ctx = CmdlineParserContext.getContext();
        ctx.addFlaggedOption("out", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, 'o', "output filename (extensions .gz and .zip are understood)");
        ctx.addSwitch("outzip", JSAP.NO_SHORTFLAG, "unzip output file on the fly");
        ctx.addSwitch("outgzip", JSAP.NO_SHORTFLAG, "gunzip output file on the fly");
    }

    @Override
    public void open() throws Exception {
        useGzip = ctx.getBoolean("outgzip");
        useZip = ctx.getBoolean("outzip");
        filename = ctx.getString("out");
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

        // command line parsed, now open the output (and check for writeability of file)
        // if the file does not exist, we terminate without doing anything
        if (filename == null) {
            filename = "(stdout)";   // have something readable
            rawStream = System.out;
        } else {
            try {
                rawStream = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                LOG.error("Cannot open file {} for output", filename);
                // fatal error, terminate
                System.exit(1);
            }
        }
        if (useGzip) {
            uncompressedStream = new GZIPOutputStream(rawStream);
        } else if (useZip) {
            ZipOutputStream zipOutput = new ZipOutputStream(rawStream);
            zipOutput.putNextEntry(new ZipEntry(filename));
            uncompressedStream = zipOutput;
        } else {
            uncompressedStream = rawStream;
        }
    }

    @Override
    public void close() throws Exception {
        if (useZip)
            ((ZipOutputStream)uncompressedStream).closeEntry();
        uncompressedStream.close();
    }
}
