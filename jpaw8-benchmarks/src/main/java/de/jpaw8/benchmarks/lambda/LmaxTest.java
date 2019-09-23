package de.jpaw8.benchmarks.lambda;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import de.jpaw8.batch.api.Batch;
import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.BatchWriterFactory;
import de.jpaw8.batch.api.Batches;
import de.jpaw8.batch.producers.BatchReaderRange;

// Benchmarks to investigate how much performance the new lambda take

//java -jar target/jpaw8-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*LmaxTest.*"
//# Run complete. Total time: 00:02:31

//Benchmark                           Mode  Samples        Score        Error  Units
//d.j.b.l.LmaxTest.jpawStreamMap      avgt        9    69354.892 ±   1621.926  ns/op
//d.j.b.l.LmaxTest.jpawStreamSetup    avgt        9       81.025 ±      0.613  ns/op
//d.j.b.l.LmaxTest.lmaxMap            avgt        9  1643093.088 ± 156223.160  ns/op
//d.j.b.l.LmaxTest.lmaxSetup          avgt        9   151717.831 ±  14879.137  ns/op




@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class LmaxTest {
    public static final long NUM = 10000;

    private static class ConsumerFactory implements BatchWriterFactory<Object> {
        private final Blackhole bh;

        private ConsumerFactory(Blackhole bh) {
            this.bh = bh;
        }

        @Override
        public BatchWriter<Object> get(int threadno) {
            return (Object l, int i) -> { bh.consume(l); };
        }
    }

//
//  Benchmarks to measure the overhead of the disruptor
//

    @Benchmark
    public void jpawBatchLmaxMap(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1, NUM).newThread().forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }
    @Benchmark
    public void jpawBatchLmaxSetup(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1,  10).newThread().forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }

    @Benchmark
    public void jpawBatchSequentialMap(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1, NUM).forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }

    @Benchmark
    public void jpawBatchSequentialSetup(Blackhole bh) throws Exception {
        Batch<Long> sequence = new BatchReaderRange(1,  10).forEach(l -> bh.consume(l));
        sequence.runNoLog();
    }

    @Benchmark
    public void jpawBatchParallelMap(Blackhole bh) throws Exception {
        Batches<Long> sequence = new BatchReaderRange(1, NUM).parallel(4, 128, new ConsumerFactory(bh));
        sequence.runNoLog();
    }

    @Benchmark
    public void jpawBatchParallelSetup(Blackhole bh) throws Exception {
        Batches<Long> sequence = new BatchReaderRange(1, 10).parallel(4, 128, new ConsumerFactory(bh));
        sequence.runNoLog();
    }


}
