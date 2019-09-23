package de.jpaw8.benchmarks.nashorn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

// Benchmarks to investigate how fast JavaScript invocation works

//java -jar target/jpaw8-benchmarks.jar -i 3 -f 3 -wf 1 -wi 3 ".*CallJS.*"

//Benchmark              Mode  Cnt   Score   Error  Units
//CallJS.invokeSimpleJS  avgt    9  79.593 ± 2.243  ns/op


@State(value = Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class CallJS {
    private ScriptEngine hornDriver = new ScriptEngineManager().getEngineByName("nashorn");
    Invocable subsystem;

    public static class MyData {
        public int longNum;
        public String text;
    }

    MyData data;

    @Setup
    public void setUp() throws IOException, ScriptException {
        hornDriver.eval("var doubleIt = function(arg) { return arg.longNum * 2; }");
        subsystem = (Invocable)hornDriver;
        data = new MyData();
        data.longNum = 12;
        data.text = "hello world";
    }

    @Benchmark
    public void invokeSimpleJS(Blackhole bh) throws NoSuchMethodException, ScriptException {
        bh.consume(subsystem.invokeFunction("doubleIt", data));
    }
}
