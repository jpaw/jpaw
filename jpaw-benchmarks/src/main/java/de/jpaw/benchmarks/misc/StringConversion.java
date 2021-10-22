package de.jpaw.benchmarks.misc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import de.jpaw.util.ByteArray;
import de.jpaw.util.Cesu8Encoder;

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*StringConversion.*"

//Benchmark                             Mode  Cnt    Score   Error  Units
//StringConversion.encodeCharset        avgt   25  150.114 ± 8.559  ns/op
//StringConversion.encodeText           avgt   25  136.742 ± 3.073  ns/op
//StringConversion.encodeCesu           avgt   25  273.303 ± 4.467  ns/op
//StringConversion.encodeCesuWithUnsafe avgt    9  296.970 ± 20.217  ns/op
//StringConversion.withCharset          avgt   25  192.677 ± 4.056  ns/op
//StringConversion.withText             avgt   25  191.273 ± 7.041  ns/op

// => WEIRD! WHY IS ENCODING VIA Charset SLOWER????????????

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StringConversion {
    public Charset utf8Charset = ByteArray.CHARSET_UTF8;
    public String utf8Name = "UTF-8";
    public String sampleText = "ksjdhf ssjkdfh sdf sdjkf sdfkjÄ skjfsÜ skdfs kdfhßsdfksjdfhsk € dfhsdklfj";
    public byte[] sampleByteArray = null;

    @Setup
    public void doSetup() throws UnsupportedEncodingException {
        sampleByteArray = sampleText.getBytes("UTF-8");
    }

    @Benchmark
    public String withText() throws UnsupportedEncodingException {
        return new String(sampleByteArray, utf8Name);
    }

    @Benchmark
    public String withCharset() {
        return new String(sampleByteArray, utf8Charset);
    }

    @Benchmark
    public byte[] encodeText() throws UnsupportedEncodingException {
        return sampleText.getBytes(utf8Name);
    }

    @Benchmark
    public byte[] encodeCharset() {
        return sampleText.getBytes(utf8Charset);
    }

    @Benchmark
    public byte[] encodeCesu() {
        return Cesu8Encoder.encodeToCesu8(sampleText);
    }
    @Benchmark
    public byte[] encodeCesu2() {
        return Cesu8Encoder.encodeToCesu8Copy(sampleText);
    }
}
