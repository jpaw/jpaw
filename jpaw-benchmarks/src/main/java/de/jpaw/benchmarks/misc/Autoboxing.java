package de.jpaw.benchmarks.misc;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

// measure the cost of an autoboxing operation

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*Autoboxing.*"

//Benchmark                   Mode  Cnt  Score   Error  Units
//Autoboxing.returnAutoboxed  avgt   25  5.286 ± 0.234  ns/op
//Autoboxing.returnNative     avgt   25  3.624 ± 0.020  ns/op

// => this benchmark does show a small but noticeable difference (jdk 1.8.0.31) Boxing takes about 1.5 ns (plus subsequent cost of GC)


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class Autoboxing {
    private static final int NUM = 1000;

    public int[] a;

    @Setup
    public void init() {
        a = new int[NUM];

        Random r = new Random(System.nanoTime());
        for (int i = 0; i < NUM; ++i) {
            a[i] = (r.nextInt() & 0xffffff) + 0x32000000;       // bit enough to ensure the values are not cached.
        }
    }


    @Benchmark
    public int returnNative() {
        return a[33];
    }

    @Benchmark
    public Integer returnAutoboxed() {
        return a[33];
    }
}
