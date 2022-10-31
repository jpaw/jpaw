package de.jpaw.benchmarks.misc;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// measure if Java String.equals checks for object identity (positive result) or hashCode (negative case) first

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*StringCompareBench.*"


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class StringCompareBench {
    private static final int NUM = 1000;
    private static final Integer ZERO = Integer.valueOf(0);
    private static final Integer ONE = Integer.valueOf(1);
    
    private final String string1 = "haksdhsakdhsajkdhFFFF";
    private final String string2 = "haksdhsakdhsajshdhjsh";

    public String[] a;  // case with identical instances
    public String[] b;  // case with same contents


    @Setup
    public void init() {
        a = new String[NUM];
        b = new String[NUM];
        for (int i = 0; i < NUM; ++i) {
            a[i] = (i & 1) != 0 ? string1 : string2;
            b[i] = new String(((i & 1) != 0 ? string1 : string2).toCharArray());  // copied arrays enforced per instance
        }
        assert a[7] == a[9];
        assert b[7] != b[9];
    }

//    private static int compareNative(final String x, final String y) {
//        return x.equals(y) ? 1 : 0;
//    }
//
//    private static int compareNative(final String[] aa) {
//        int sum = 0;
//        for (int i = 0; i < NUM; ++i) {
//            for (int j = 0; j < NUM; ++j) {
//                sum += compareNative(aa[i], aa[j]);
//            }
//        }
//        return sum;
//    }
//
//    @Benchmark
//    public void benchNativeIdentical(Blackhole bh) {
//        bh.consume(compareNative(a));
//    }
//
//    @Benchmark
//    public void benchNativeCopies(Blackhole bh) {
//        bh.consume(compareNative(b));
//    }

    private static int compareAndAggregate(final String[] aa, BiFunction<String, String, Integer> f) {
        int sum = 0;
        for (int i = 0; i < NUM; ++i) {
            for (int j = 0; j < NUM; ++j) {
                sum += f.apply(aa[i], aa[j]);
            }
        }
        return sum;
    }

    private static Integer compareOnlyIdentity(final String x, final String y) {
        return x == y ? ONE : ZERO;
    }

    private static Integer compareNative(final String x, final String y) {
        return x.equals(y) ? ONE : ZERO;
    }

    private static Integer compareIdentity(final String x, final String y) {
        return x == y || x.equals(y) ? ONE : ZERO;
    }

    private static Integer compareHash(final String x, final String y) {
        return x.hashCode() == y.hashCode() && x.equals(y) ? ONE : ZERO;
    }

    private static Integer compareBoth(final String x, final String y) {
        return x == y ? ONE : x.hashCode() != y.hashCode() ? ZERO : x.equals(y) ? ONE : ZERO;
    }

    @Benchmark
    public void benchNativeBaseline(Blackhole bh) {
        bh.consume(compareAndAggregate(a, StringCompareBench::compareOnlyIdentity));
    }

    @Benchmark
    public void benchNativeIdentical(Blackhole bh) {
        bh.consume(compareAndAggregate(a, StringCompareBench::compareNative));
    }

    @Benchmark
    public void benchNativeCopies(Blackhole bh) {
        bh.consume(compareAndAggregate(b, StringCompareBench::compareNative));
    }

    @Benchmark
    public void benchIdentityIdentical(Blackhole bh) {
        bh.consume(compareAndAggregate(a, StringCompareBench::compareIdentity));
    }

    @Benchmark
    public void benchIdentityCopies(Blackhole bh) {
        bh.consume(compareAndAggregate(b, StringCompareBench::compareIdentity));
    }

    @Benchmark
    public void benchHashIdentical(Blackhole bh) {
        bh.consume(compareAndAggregate(a, StringCompareBench::compareHash));
    }

    @Benchmark
    public void benchHashCopies(Blackhole bh) {
        bh.consume(compareAndAggregate(b, StringCompareBench::compareHash));
    }

    @Benchmark
    public void benchBothIdentical(Blackhole bh) {
        bh.consume(compareAndAggregate(a, StringCompareBench::compareBoth));
    }

    @Benchmark
    public void benchBothCopies(Blackhole bh) {
        bh.consume(compareAndAggregate(b, StringCompareBench::compareBoth));
    }
}
