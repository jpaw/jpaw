package de.jpaw.benchmarks.misc;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*Hashes.*"

//Benchmark                          Mode  Samples     Score   Error  Units
//d.j.b.m.Hashes.byteHash            avgt       25  1113.135 ± 0.286  ns/op
//d.j.b.m.Hashes.intHash             avgt       25  1114.830 ± 1.197  ns/op
//d.j.b.m.Hashes.longerStringHash    avgt       25     1.554 ± 0.011  ns/op
//d.j.b.m.Hashes.shortStringHash     avgt       25     1.554 ± 0.012  ns/op

// average sizes: 14.6 bytes for short string, 34.6 bytes for long string

// => String hash not length sensitive?

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class Hashes {
    private static final int NUM = 1000;

    public static AtomicBoolean conflict = new AtomicBoolean();
    public static int [] a;
    public static byte [] b;
    public static String [] c;      // 14 chars +/- 1
    public static String [] d;      // 32 chars +/- 2
    int sizeC = 0;
    int sizeD = 0;

    @Setup
    public void init() {
        if (conflict.getAndSet(true))
            throw new RuntimeException("incorrect setup - only a single thread of this test should run due to the use of static variables");
        a = new int[NUM];
        b = new byte[NUM];
        c = new String[NUM];
        d = new String[NUM];

        Random r = new Random(System.nanoTime());
        for (int i = 0; i < NUM; ++i) {
            a[i] = (r.nextInt() & 0xffffff) + 0x32000000;
            b[i] = (byte)(r.nextInt() & 0xff);
            c[i] = "Hello world " + b[i];
            d[i] = "X " + a[i] + c[i] + "jsgdfjsjs";
            sizeC += c[i].length();
            sizeD += d[i].length();
        }
    }

//    @TearDown
//    public void info() {
//        System.out.println("Tested size: " + NUM + " members, short strings total length = " + sizeC + ", long strings total size = " + sizeD);
//    }

    @Benchmark
    public void intHash(Blackhole bh) {
        bh.consume(Arrays.hashCode(a));
    }

    @Benchmark
    public void byteHash(Blackhole bh) {
        bh.consume(Arrays.hashCode(b));
    }

    @Benchmark
    @OperationsPerInvocation(NUM)
    public void shortStringHash(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += c[i].hashCode();
        bh.consume(sum);
    }

    @Benchmark
    @OperationsPerInvocation(NUM)
    public void longerStringHash(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += d[i].hashCode();
        bh.consume(sum);
    }
}
