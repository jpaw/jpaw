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
import org.openjdk.jmh.infra.Blackhole;

// measure the difference between bitwise and and modulus operations


//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*ModVsAnd.*"

//Benchmark                        Mode  Samples     Score    Error  Units
//d.j.b.m.ModVsAnd.bitwiseConst    avgt       25   575.967 ±  6.350  ns/op
//d.j.b.m.ModVsAnd.bitwiseRand     avgt       25   571.838 ±  2.031  ns/op
//d.j.b.m.ModVsAnd.modulusConst    avgt       25  3077.503 ±  6.317  ns/op
//d.j.b.m.ModVsAnd.modulusRand     avgt       25  3080.649 ± 13.250  ns/op

// => modulus takes 2.5 ns more per operation

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class ModVsAnd {
    private static final int NUM = 1000;

    public int [] a;
    public int [] b;
    public int [] c;


    @Setup
    public void init() {
        a = new int[NUM];
        b = new int[NUM];
        c = new int[NUM];

        Random r = new Random(System.nanoTime());
        for (int i = 0; i < NUM; ++i) {
            a[i] = (r.nextInt() & 0xffff) + 0x3240000;
            b[i] = (r.nextInt() & 0xffff) + 0x0010000;
            c[i] = 100000;
        }
    }

    @Benchmark
    public void modulusRand(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += a[i] % b[i];
        bh.consume(sum);
    }

    @Benchmark
    public void bitwiseRand(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += a[i] & b[i];
        bh.consume(sum);
    }

    @Benchmark
    public void modulusConst(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += a[i] % c[i];
        bh.consume(sum);
    }

    @Benchmark
    public void bitwiseConst(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i)
            sum += a[i] & c[i];
        bh.consume(sum);
    }
}
