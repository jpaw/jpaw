package de.jpaw8.benchmarks.datetime;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how much performance of getting the current time

//java -jar target/jpaw8-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*TimeTestInNanos.*"
//Benchmark                                     Mode  Samples    Score  Score error  Units
//d.j.b.d.TimeTestInNanos.javaNow               avgt        9  219.191        4.943  ns/op
//d.j.b.d.TimeTestInNanos.javaNowInstant        avgt        9   41.318        0.536  ns/op
//d.j.b.d.TimeTestInNanos.javaNowUTC            avgt        9  106.880        2.548  ns/op
//d.j.b.d.TimeTestInNanos.javaSystemMillis      avgt        9   32.379        0.084  ns/op
//d.j.b.d.TimeTestInNanos.javaSystemNanoTime    avgt        9   30.110      ± 0.053  ns/op  faster than millis! In contrast to the net
//d.j.b.d.TimeTestInNanos.jodaNew               avgt        9   49.262        4.066  ns/op
//d.j.b.d.TimeTestInNanos.jodaNow               avgt        9   49.048        1.979  ns/op
//d.j.b.d.TimeTestInNanos.jodaNowInstant        avgt        9   34.641        0.484  ns/op
//d.j.b.d.TimeTestInNanos.jodaNowUTC            avgt        9   40.655        0.699  ns/op


// Setting which may impact this:  http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
// General overview: http://www.oracle.com/technetwork/java/whitepaper-135217.html
// Details: http://stas-blogspot.blogspot.de/2011/07/most-complete-list-of-xx-options-for.html


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class TimeTestInNanos {

//
//  Benchmarks to measure the overhead to get a timestamp, for Joda and Java8 time
//
    // see: (old) http://stackoverflow.com/questions/19052316/why-is-system-nanotime-way-slower-in-performance-than-system-currenttimemill
    // see: (new) https://groups.google.com/forum/#!topic/mechanical-sympathy/44Pc1u0YU8I
    @Benchmark
    public void javaSystemNanoTime(Blackhole bh) {
        bh.consume(System.nanoTime());
    }

    // see https://blogs.oracle.com/ksrini/entry/we_take_java_performance_very
    // http://bugs.java.com/view_bug.do?bug_id=6876279
    @Benchmark
    public void javaSystemMillis(Blackhole bh) {
        bh.consume(System.currentTimeMillis());
    }

    @Benchmark
    public void jodaNow(Blackhole bh) {
        bh.consume(org.joda.time.LocalDateTime.now());
    }

    @Benchmark
    public void jodaNowUTC(Blackhole bh) {
        bh.consume(org.joda.time.LocalDateTime.now(org.joda.time.DateTimeZone.UTC));
    }

    @Benchmark
    public void jodaNowInstant(Blackhole bh) {
        bh.consume(org.joda.time.Instant.now());
    }

    // requires Java 8 support
    @Benchmark
    public void javaNow(Blackhole bh) {
        bh.consume(java.time.LocalDateTime.now());
    }

    // requires Java 8 support
    @Benchmark
    public void javaNowUTC(Blackhole bh) {
        bh.consume(java.time.LocalDateTime.now(java.time.ZoneId.of("Z")));
    }

    // requires Java 8 support
    @Benchmark
    public void javaNowInstant(Blackhole bh) {
        bh.consume(java.time.Instant.now());
    }

    @Benchmark
    public void jodaNew(Blackhole bh) {
        bh.consume(new org.joda.time.LocalDateTime());
    }
}
