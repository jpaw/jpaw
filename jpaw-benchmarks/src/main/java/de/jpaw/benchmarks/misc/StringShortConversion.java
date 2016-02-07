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

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*StringShortConversion.*"

//Benchmark                                   Mode  Cnt   Score   Error  Units
//StringShortConversion.encodeCharset         avgt   25  68.337 ± 4.884  ns/op
//StringShortConversion.encodeText            avgt   25  61.552 ± 1.516  ns/op
//StringShortConversion.encodeCesu            avgt   25  50.922 ± 2.359  ns/op
//StringShortConversion.encodeCesuWithUnsafe  avgt    9  48.364 ± 8.535  ns/op
//StringShortConversion.withCharset           avgt   25  83.720 ± 2.675  ns/op
//StringShortConversion.withText              avgt   25  82.953 ± 1.410  ns/op

// => WEIRD! WHY IS ENCODING VIA Charset SLOWER????????????

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StringShortConversion {
    public Charset utf8Charset = ByteArray.CHARSET_UTF8;
    public String utf8Name = "UTF-8";
    public String sampleText = "jÄskjÜ€h";
    public byte [] sampleByteArray = null;

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
    public byte [] encodeText() throws UnsupportedEncodingException {
        return sampleText.getBytes(utf8Name);
    }

    @Benchmark
    public byte [] encodeCharset() {
        return sampleText.getBytes(utf8Charset);
    }

    @Benchmark
    public byte [] encodeCesu() {
        return Cesu8Encoder.encodeToCesu8(sampleText);
    }
    @Benchmark
    public byte [] encodeCesu2() {
        return Cesu8Encoder.encodeToCesu8Copy(sampleText);
    }
    @Benchmark
    public byte [] encodeCesuWithUnsafe() {
        return Cesu8Encoder.encodeToCesu8WithUnsafe(sampleText);
    }
//    @Benchmark
//    public byte [] encodeCesuDirect() {
//        return Cesu8DirectEncoder.encodeToCesu8(sampleText);
//    }
}
