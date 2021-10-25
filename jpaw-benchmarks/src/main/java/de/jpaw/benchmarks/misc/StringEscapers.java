package de.jpaw.benchmarks.misc;

import java.util.concurrent.TimeUnit;

import org.apache.commons.text.StringEscapeUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw.util.Escape2Java;


//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*StringEscapers.*"
//Result "de.jpaw.benchmarks.misc.StringEscapers.escape2JavaJpaw5":
//    22709.399 ±(99.9%) 1766.969 ns/op [Average]
//    (min, avg, max) = (20997.193, 22709.399, 27614.528), stdev = 2358.853
//    CI (99.9%): [20942.430, 24476.368] (assumes normal distribution)
//
//
//  # Run complete. Total time: 00:04:22
//
//  Benchmark                          Mode  Cnt      Score       Error  Units
//  StringEscapers.escape2JavaApache1  avgt   25  28864.712 ±  2723.007  ns/op
//  StringEscapers.escape2JavaApache5  avgt   25  89042.433 ± 15157.498  ns/op
//  StringEscapers.escape2JavaJpaw1    avgt   25  11091.560 ±   134.707  ns/op
//  StringEscapers.escape2JavaJpaw5    avgt   25  22709.399 ±  1766.969  ns/op


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StringEscapers {
    private static final int N = 280;
    private static final String[] strings1 = new String[N];
    private static final String[] strings5 = new String[N];

    @Setup
    public void doSetup()  {
        // setup an array of simple 1 char strings
        for (int i = 0; i < N; ++i) {
            strings1[i] = String.valueOf((char)i);
            StringBuilder sb = new StringBuilder(5);
            sb.append((char)i);
            sb.append((char)i);
            sb.append((char)i);
            sb.append((char)i);
            sb.append((char)i);
            strings5[i] = sb.toString();
        }
    }

    @Benchmark
    public void escape2JavaApache1(Blackhole bh) {
        for (int i = 0; i < N; ++i) {
            bh.consume(StringEscapeUtils.escapeJava(strings1[i]));
        }
    }

    @Benchmark
    public void escape2JavaJpaw1(Blackhole bh) {
        for (int i = 0; i < N; ++i) {
            bh.consume(Escape2Java.escapeString2Java(strings1[i]));
        }
    }

    @Benchmark
    public void escape2JavaApache5(Blackhole bh) {
        for (int i = 0; i < N; ++i) {
            bh.consume(StringEscapeUtils.escapeJava(strings5[i]));
        }
    }

    @Benchmark
    public void escape2JavaJpaw5(Blackhole bh) {
        for (int i = 0; i < N; ++i) {
            bh.consume(Escape2Java.escapeString2Java(strings5[i]));
        }
    }
}
