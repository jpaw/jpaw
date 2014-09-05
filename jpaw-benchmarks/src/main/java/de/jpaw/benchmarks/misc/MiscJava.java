package de.jpaw.benchmarks.misc;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/** Miscellaneous benchmarks, to find out which implementation of alternative standard Java stuff is faster. */

//java -jar target/fixedpoint-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*MiscJava.*"

//Benchmark                                    Mode  Samples   Score  Score error  Units
//d.j.b.m.MiscJava.fourCharAppends             avgt        6  10.470        0.317  ns/op
//d.j.b.m.MiscJava.justReset                   avgt        6   2.508        0.013  ns/op
//d.j.b.m.MiscJava.singleStringAppend2Chars    avgt        6   9.175        0.027  ns/op
//d.j.b.m.MiscJava.singleStringAppend4Chars    avgt        6   9.181        0.776  ns/op
//d.j.b.m.MiscJava.singleStringAppend6Chars    avgt        6   9.217        0.075  ns/op
//d.j.b.m.MiscJava.singleStringAppend8Chars    avgt        6   9.286        0.379  ns/op
//d.j.b.m.MiscJava.threeCharAppends            avgt        6   8.467        1.293  ns/op
//d.j.b.m.MiscJava.twoCharAppends              avgt        6   6.731        0.240  ns/op
//=> 2 char appends take 4.1 ns, a String append takes 6.5 ns => up to 3 chars, single appends will be faster, above that, use String appends

@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class MiscJava {
    
    private StringBuilder sb = new StringBuilder();

    @Benchmark
    public void justReset(Blackhole bh) {
        sb.setLength(0);
        bh.consume(sb);
    }
    
    @Benchmark
    public void twoCharAppends(Blackhole bh) {
        sb.setLength(0);
        sb.append('.');
        sb.append(' ');
        bh.consume(sb);
    }
    
    @Benchmark
    public void threeCharAppends(Blackhole bh) {
        sb.setLength(0);
        sb.append('.');
        sb.append('d');
        sb.append(' ');
        bh.consume(sb);
    }
    
    @Benchmark
    public void fourCharAppends(Blackhole bh) {
        sb.setLength(0);
        sb.append('.');
        sb.append(' ');
        sb.append('A');
        sb.append('x');
        bh.consume(sb);
    }
    
    @Benchmark
    public void singleStringAppend2Chars(Blackhole bh) {
        sb.setLength(0);
        sb.append(". ");
        bh.consume(sb);
    }
    @Benchmark
    public void singleStringAppend4Chars(Blackhole bh) {
        sb.setLength(0);
        sb.append(". Ax");
        bh.consume(sb);
    }    
    @Benchmark
    public void singleStringAppend6Chars(Blackhole bh) {
        sb.setLength(0);
        sb.append(". AxDD");
        bh.consume(sb);
    }
    @Benchmark
    public void singleStringAppend8Chars(Blackhole bh) {
        sb.setLength(0);
        sb.append(". Ax87!k");
        bh.consume(sb);
    }
}
