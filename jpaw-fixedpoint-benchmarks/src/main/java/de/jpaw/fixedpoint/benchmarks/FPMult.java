package de.jpaw.fixedpoint.benchmarks;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

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

import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.fixedpoint.types.MilliUnits;



//Benchmarks to evaluate fixed point arithmetic vs. BigDecimal

//java -Djava.library.path=$HOME/lib -jar target/fixedpoint-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*FPMult.*"

//# Run complete. Total time: 00:03:02
//
//Benchmark                                    Mode  Samples   Score  Score error  Units
//d.j.f.b.FPMult.countLeadingBits              avgt        9   2.112        0.127  ns/op
//d.j.f.b.FPMult.multFPintMult                 avgt        9   4.416        0.191  ns/op
//d.j.f.b.FPMult.multFPsubNoScale              avgt        9   7.673        0.072  ns/op
//d.j.f.b.FPMult.multFPsubWithScale            avgt        9  19.535        0.353  ns/op
//d.j.f.b.FPMult.multFPsubWithScaleWithBD      avgt        9  96.079        1.749  ns/op
//d.j.f.b.FPMult.multFPsubWithScaleWithBest    avgt        9  21.453        0.981  ns/op
//d.j.f.b.FPMult.roundFast                     avgt        9  17.138        0.269  ns/op
//d.j.f.b.FPMult.roundSlow                     avgt        9  18.131        0.367  ns/op
//d.j.f.b.FPMult.roundNot                      avgt        9   4.866        0.340  ns/op

// NEW:
//Benchmark                                  Mode  Samples   Score  Score error  Units
//d.j.f.b.FPMult.countLeadingBits            avgt        9   2.189        0.483  ns/op
//d.j.f.b.FPMult.multFPintMult               avgt        9   4.363        0.139  ns/op
//d.j.f.b.FPMult.multFPsubNoScale            avgt        9   4.012        0.017  ns/op
//d.j.f.b.FPMult.multFPsubWithScale          avgt        9  21.637        1.200  ns/op
//d.j.f.b.FPMult.multFPsubWithScaleWithBD    avgt        9  96.578        1.462  ns/op
//d.j.f.b.FPMult.roundFast                   avgt        9  17.073        0.300  ns/op
//d.j.f.b.FPMult.roundNot                    avgt        9   4.915        0.420  ns/op
//d.j.f.b.FPMult.roundSlow                   avgt        9  18.219        0.318  ns/op

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@OperationsPerInvocation(10)

public class FPMult {

    long [] testNums = {  0L, 1L, 237648264832L, 25L, -444L, -723428748764827364L, 87264L, 242987492L, -55558963L, 87326487543L };
    MicroUnits [] testMicros = new MicroUnits[10];
    MilliUnits [] testMillis = new MilliUnits[10];

    @Setup
    public void setUp() throws UnsupportedEncodingException {
        for (int i = 0; i < 10; ++i) {
            testMicros[i] = MicroUnits.of(testNums[i]);
            testMillis[i] = MilliUnits.of(testNums[i]);
        }
    }

    @TearDown
    public void tearDown() {
    }

    @Benchmark
    public void countLeadingBits(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(Long.numberOfLeadingZeros(testNums[i]));
    }

//    @Benchmark
//    public void multFPNoScale(Blackhole bh) {
//        for (int i = 0; i < testNums.length; ++i)
//            bh.consume(testMicros[i].multiply(testMillis[9-i], RoundingMode.UNNECESSARY));
//    }

    @Benchmark
    public void multFPintMult(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(testMicros[i].multiply(3));
    }
    @Benchmark
    public void multFPsubNoScale(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(testMicros[i].mantissa_of_multiplication(testMillis[9-i], 9, RoundingMode.UNNECESSARY));
    }

    @Benchmark
    public void multFPsubWithScale(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(testMicros[i].mantissa_of_multiplication(testMillis[9-i], 3, RoundingMode.DOWN));
    }

    @Benchmark
    public void multFPsubWithScaleWithBD(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(testMicros[i].mantissa_of_multiplication_using_BD(testMillis[9-i], 3, RoundingMode.DOWN));
    }

    @Benchmark
    public void roundFast(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(MilliUnits.of(testMicros[i], RoundingMode.DOWN));
    }

    @Benchmark
    public void roundSlow(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(MilliUnits.of(testMicros[i], RoundingMode.HALF_EVEN));
    }

    @Benchmark
    public void roundNot(Blackhole bh) {
        for (int i = 0; i < testNums.length; ++i)
            bh.consume(MicroUnits.of(testMillis[i], RoundingMode.UNNECESSARY));
    }
}
