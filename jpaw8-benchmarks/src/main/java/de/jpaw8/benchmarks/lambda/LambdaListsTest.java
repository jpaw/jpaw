package de.jpaw8.benchmarks.lambda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how much performance the new lambda take

//java -jar target/jpaw8-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*LambdaListsTest.*"
//# Run complete. Total time: 00:02:31
//
//Benchmark                                             Mode  Samples    Score  Score error  Units


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class LambdaListsTest {
    static class IndexedString {
        public final Integer ind;
        public final String text;
        public IndexedString(int i, String t) {
            ind = i;
            text = t;
        }
    }

    private List<IndexedString> n1;
    private List<IndexedString> n100;
    private List<IndexedString> n10000;
    private List<IndexedString> n1000000;
    private final List<List<IndexedString>> offsets = new ArrayList<>(10);

    private List<IndexedString> init(int num) {
        List<IndexedString> result = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            result.add(new IndexedString(i, "This is string " + i));
        }
        return result;
    }

    @Setup
    public void setUp() throws IOException {
        n1       = init(1);
        n100     = init(100);
        n10000   = init(10000);
        n1000000 = init(1000000);
        offsets.add(n1);
        offsets.add(null);
        offsets.add(n100);
        offsets.add(null);
        offsets.add(n10000);
        offsets.add(null);
        offsets.add(n1000000);
    }

    @Param({"0", "2", "4", "6"})
    public int exponent;

    /** Computes the initial size for a HashMap, given the known number of elements. */
    private int getIdealInitialSize(final int maxElements) {
        return (int) (maxElements * 1.5 + 1);
    }

//
//  Benchmarks to measure the overhead of lambdas in standard processing scenarios
//  Filter: extract an element of an object list, and collect it into a list of just that element
//  Index:  create a map of the objects, indexed by an element of the object
//

    @Benchmark
    public void javaFilterClassicWithKnownResultSize(Blackhole bh) {
        // classic java approach: preallocate the target, loop
        final List<IndexedString> data = offsets.get(exponent);
        final List<Integer> filtered = new ArrayList<>(data.size());
        for (IndexedString obj: data) filtered.add(obj.ind);
        bh.consume(filtered);
    }
    @Benchmark
    public void javaFilterClassicWithDynamicResultSize(Blackhole bh) {
        // classic java approach: preallocate the target, with small initial size, then loop (causes GC overhead due to reallocations)
        final List<IndexedString> data = offsets.get(exponent);
        final List<Integer> filtered = new ArrayList<>();
        for (IndexedString obj: data) filtered.add(obj.ind);
        bh.consume(filtered);
    }
    @Benchmark
    public void javaFilterStreamWithKnownResultSize(Blackhole bh) {
        // stream approach, but spend some more effort into preallocating the result structure
        final List<IndexedString> data = offsets.get(exponent);
        final List<Integer> filtered = data.stream().map(x -> x.ind).collect(Collectors.toCollection(() -> new ArrayList<Integer>(data.size())));
        bh.consume(filtered);
    }
    @Benchmark
    public void javaFilterStreamWithDynamicResultSize(Blackhole bh) {
        // The streams approach seen most often (it is the shortest to write)
        final List<IndexedString> data = offsets.get(exponent);
        final List<Integer> filtered = data.stream().map(x -> x.ind).collect(Collectors.toList());
        bh.consume(filtered);
    }



    @Benchmark
    public void javaIndexClassicWithKnownResultSize(Blackhole bh) {
        // classic java approach: preallocate the target, loop
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(data.size());
        for (IndexedString obj: data) indexed.put(obj.ind, obj);
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexClassicWithKnownResultSizeScaled(Blackhole bh) {
        // classic java approach: preallocate the target, loop
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(getIdealInitialSize(data.size()));
        for (IndexedString obj: data) indexed.put(obj.ind, obj);
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexClassicWithDynamicResultSize(Blackhole bh) {
        // classic java approach: preallocate the target, with small initial size, then loop (causes GC overhead due to reallocations)
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>();
        for (IndexedString obj: data) indexed.put(obj.ind, obj);
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexStreamWithKnownResultSize(Blackhole bh) {
        // streams approach with forEach
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(data.size());
        data.stream().forEach(obj -> indexed.put(obj.ind,  obj));
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexStreamWithKnownResultSizeScaled(Blackhole bh) {
        // streams approach with forEach
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(getIdealInitialSize(data.size()));
        data.stream().forEach(obj -> indexed.put(obj.ind,  obj));
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexForeachWithKnownResultSize(Blackhole bh) {
        // functional approach with forEach directly applied to collection
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(data.size());
        data.forEach(obj -> indexed.put(obj.ind,  obj));
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexForeachWithKnownResultSizeScaled(Blackhole bh) {
        // functional approach with forEach directly applied to collection
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = new HashMap<>(getIdealInitialSize(data.size()));
        data.forEach(obj -> indexed.put(obj.ind,  obj));
        bh.consume(indexed);
    }
    @Benchmark
    public void javaIndexStreamWithDynamicResultSize(Blackhole bh) {
        // The streams approach seen most often (it is the shortest to write)
        final List<IndexedString> data = offsets.get(exponent);
        final Map<Integer, IndexedString> indexed = data.stream().collect(Collectors.toMap(o -> o.ind, o -> o));
        bh.consume(indexed);
    }
}
