package de.jpaw.benchmarks.misc;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

// measure the cost of random number / UUID creation, single threaded as well as multi threaded.

// java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*BenchRandom.*"
//
// results with JDK 11:
//Benchmark                       Mode  Cnt     Score   Error  Units
//BenchRandom.random              avgt    2    18.772          ns/op
//BenchRandom.random8             avgt    2    28.946          ns/op
//BenchRandom.randomThreadLocal   avgt    2     3.851          ns/op
//BenchRandom.randomThreadLocal8  avgt    2     9.851          ns/op
//BenchRandom.randomUUID          avgt    2   375.304          ns/op
//BenchRandom.randomUUID8         avgt    2  6721.986          ns/op

//results with JDK 17:
//Benchmark                       Mode  Cnt     Score   Error  Units
//BenchRandom.random              avgt    2    18.694          ns/op
//BenchRandom.random8             avgt    2    29.519          ns/op
//BenchRandom.randomThreadLocal   avgt    2     4.164          ns/op
//BenchRandom.randomThreadLocal8  avgt    2    10.595          ns/op
//BenchRandom.randomUUID          avgt    2   372.302          ns/op
//BenchRandom.randomUUID8         avgt    2  5571.595          ns/op


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BenchRandom {
    public Random r;

    @Setup
    public void init() {
        r = new Random(System.nanoTime());
    }


    @Benchmark
    public void random(Blackhole bh) {
        bh.consume(r.nextLong());
    }

    @Threads(8)
    @Benchmark
    public void random8(Blackhole bh) {
        bh.consume(r.nextLong());
    }

    @Benchmark
    public void randomThreadLocal(Blackhole bh) {
        bh.consume(ThreadLocalRandom.current().nextLong());
    }

    @Threads(8)
    @Benchmark
    public void randomThreadLocal8(Blackhole bh) {
        bh.consume(ThreadLocalRandom.current().nextLong());
    }

    @Benchmark
    public void randomUUID(Blackhole bh) {
        bh.consume(UUID.randomUUID());
    }

    @Threads(8)
    @Benchmark
    public void randomUUID8(Blackhole bh) {
        bh.consume(UUID.randomUUID());
    }
}
