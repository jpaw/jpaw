package de.jpaw8.benchmarks.lambda;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw8.batch.api.Batch;
import de.jpaw8.batch.producers.BatchReaderRange;

// Benchmarks to investigate how much performance the new lambda take

//java -jar target/jpaw8-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*StreamsTest.*"
//# Run complete. Total time: 00:02:31

//Benchmark                                Mode  Samples       Score       Error  Units
//d.j.b.b.l.StreamsTest.javaStreamMap      avgt        9   76979.885 ±  9473.924  ns/op
//d.j.b.b.l.StreamsTest.javaStreamSetup    avgt        9     105.675 ±     2.152  ns/op
//d.j.b.b.l.StreamsTest.jpawStreamMap      avgt        9  186448.712 ± 10776.306  ns/op
//d.j.b.b.l.StreamsTest.jpawStreamSetup    avgt        9     113.045 ±    16.611  ns/op



@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StreamsTest {
    public static final long NUM = 10000;


//
//  Benchmarks to measure the overhead to run a loop, in standard and in lambda mode
//

    @Benchmark
    public void javaStreamSequentialMap(Blackhole bh) {
        LongStream.rangeClosed(1, NUM)         .map(l -> l* l).forEach(l -> bh.consume(l));
    }
    @Benchmark
    public void javaStreamSequentialSetup(Blackhole bh) {
        LongStream.rangeClosed(1,  10)         .map(l -> l* l).forEach(l -> bh.consume(l));
    }

    @Benchmark
    public void jpawBatchSequentialMap(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1, NUM).map(l -> l * l).forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }

    @Benchmark
    public void jpawBatchSequentialSetup(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1,  10).map(l -> l * l).forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }

}
