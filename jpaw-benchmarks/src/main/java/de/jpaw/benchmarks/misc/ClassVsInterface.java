package de.jpaw.benchmarks.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

// measure the difference between method call via interface compare to method call to class directly
// rumors are that interface calls are slower: http://stackoverflow.com/questions/6839943/why-are-interface-method-invocations-slower-than-concrete-invocations

//java -jar target/jpaw-benchmarks.jar -i 5 -f 5 -wf 3 -wi 3 ".*ClassVsInterface.*"

//Benchmark                       Mode  Cnt    Score    Error  Units
//ClassVsInterface.callClass      avgt   25  988.002 ± 11.016  ns/op
//ClassVsInterface.callInterface  avgt   25  992.595 ± 24.158  ns/op

// => this benchmark does not show any significant difference (jdk 1.8.0.31)


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class ClassVsInterface {
    public static Map<Integer, Integer> myMapI = new ConcurrentHashMap<Integer, Integer>(1000);
    public static ConcurrentHashMap<Integer, Integer> myMapC = new ConcurrentHashMap<Integer, Integer>(1000);

    @Setup
    public void setup() {
        for (int i = 0; i < 1000; ++i) {
            Integer key = Integer.valueOf((31 * i) % 997);
            Integer val = Integer.valueOf(i);
            myMapC.put(key, val);
            myMapI.put(key, val);
        }
    }

    @Benchmark
    public int callInterface() {
        int sum = 0;
        for (int i = 30; i < 130; ++i) {
            Integer key = Integer.valueOf((31 * i * 7) % 997);
            sum += myMapI.get(key);
        }
        return sum;
    }

    @Benchmark
    public int callClass() {
        int sum = 0;
        for (int i = 30; i < 130; ++i) {
            Integer key = Integer.valueOf((31 * i * 7) % 997);
            sum += myMapC.get(key);
        }
        return sum;
    }
}
