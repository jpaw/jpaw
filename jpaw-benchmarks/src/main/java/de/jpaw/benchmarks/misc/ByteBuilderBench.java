package de.jpaw.benchmarks.misc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.util.ByteBuilder;

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*ByteBuilderBench.*"
// NUM = 2000
//Benchmark                 Mode  Cnt       Score      Error  Units
//ByteBuilderBench.charBb   avgt   25    2327.628 ±   34.083  ns/op     => 70 times faster than dos
//ByteBuilderBench.charDos  avgt   25  147520.524 ± 1282.078  ns/op
//ByteBuilderBench.intBb    avgt   25    3792.124 ±  171.531  ns/op     => 15 times
//ByteBuilderBench.intDos   avgt   25  155340.271 ± 2470.023  ns/op

// NUM = 100
//Benchmark                 Mode  Cnt     Score    Error  Units
//ByteBuilderBench.charBb   avgt   25   129.326 ±  2.296  ns/op     => just 4 times faster than dos
//ByteBuilderBench.charDos  avgt   25  7345.663 ±  8.673  ns/op
//ByteBuilderBench.intBb    avgt   25   189.048 ±  3.109  ns/op     => 10 times
//ByteBuilderBench.intDos   avgt   25  7782.464 ± 30.116  ns/op

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class ByteBuilderBench {
    private static final int NUM = 100;

    public static int offset = 17;
    public static ByteBuilder bb = new ByteBuilder(4 * NUM, null);
    public static ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * NUM);
    public static DataOutputStream dos = new DataOutputStream(baos); 

    @Benchmark
    public void intBb(Blackhole bh) {
        bb.setLength(0);
        final int o = offset;
        for (int i = 0; i < NUM; ++i)
            bb.writeInt(i + o);
        bh.consume(bb.getCurrentBuffer());
    }

    @Benchmark
    public void charBb(Blackhole bh) {
        bb.setLength(0);
        final int o = offset;
        for (int i = 0; i < 4 * NUM; ++i)
            bb.writeByte(i + o);
        bh.consume(bb.getCurrentBuffer());
    }
    
    @Benchmark
    public void intDos(Blackhole bh) throws IOException {
        baos.reset();
        final int o = offset;
        for (int i = 0; i < NUM; ++i)
            dos.writeInt(i + o);
        bh.consume(bb.getCurrentBuffer());
    }

    @Benchmark
    public void charDos(Blackhole bh) throws IOException {
        baos.reset();
        final int o = offset;
        for (int i = 0; i < 4 * NUM; ++i)
            dos.writeByte(i + o);
        bh.consume(bb.getCurrentBuffer());
    }
}
