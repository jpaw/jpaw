package de.jpaw8.batch.integrationtests;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw8.batch.api.BatchProcessorFactory;
import de.jpaw8.batch.api.BatchWriter;
import de.jpaw8.batch.api.Batches;
import de.jpaw8.batch.consumers.impl.BatchProcessorFactoryByIdentity;
import de.jpaw8.batch.consumers.impl.BatchWriterConsumer;
import de.jpaw8.batch.consumers.impl.BatchWriterFactoryByIdentity;
import de.jpaw8.batch.factories.Collector;
import de.jpaw8.batch.filters.BatchFilterDelay;
import de.jpaw8.batch.producers.BatchReaderRange;
import de.jpaw8.batch.producers.impl.BatchReaderNewThreadsViaQueue;


public class SingleRequest {
    static private class Adder implements Consumer<Long> {
        long sum = 0L;

        @Override
        public void accept(Long t) {
//            System.out.println("Got  " + t);
            sum += t.longValue();
        }
    }
    static private class Counter implements Consumer<Object> {
        int num = 0;

        @Override
        public void accept(Object t) {
            ++num;
        }
    }
    static private class ParallelCounter implements Consumer<Object> {
        AtomicInteger num = new AtomicInteger();

        @Override
        public void accept(Object t) {
            num.incrementAndGet();
        }
    }
    static private class Delay implements Predicate<Object> {

        @Override
        public boolean test(Object t) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted: " + e);
            }
            return true;
        }
    }
    static private class DelayFunction<T> implements Function<T,T> {

        @Override
        public T apply(T t) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted: " + e);
            }
            return t;
        }
    }

    @Test
    public void testCounterSimple() throws Exception {
        Counter a = new Counter();
        new BatchReaderRange(1L, 2000L).forEach(a).run();
        Assert.assertEquals(a.num, 2000);
    }

    @Test
    public void testCounterMap() throws Exception {
        Counter a = new Counter();
        new BatchReaderRange(1L, 2000L).map(l -> l * l).forEach(a).run();
        Assert.assertEquals(a.num, 2000);
    }

    @Test
    public void testCounterFilter() throws Exception {
        Counter a = new Counter();
        new BatchReaderRange(1L, 2000L).filter(l -> (l & 1) == 0L).forEach(a).run();
        Assert.assertEquals(a.num, 1000);
    }

    @Test
    public void testCounter() throws Exception {
        Counter a = new Counter();
        new BatchReaderRange(1L, 2000L).filter(l -> (l & 1) == 0L).map(l -> l * l).forEach(a).run();
        Assert.assertEquals(a.num, 1000);
    }

    @Test
    public void testAdder() throws Exception {
        Adder a = new Adder();
        new BatchReaderRange(1L, 100L).filter(l -> (l & 1) == 0L).map(l -> l * l).forEach(a).run();
//        Batch sequence = new BatchRange(1L, 100L).filter(l -> (l & 1) == 0L).map(l -> { System.out.println("Got " + l); return l * l; }).forEach(a);
        Assert.assertEquals(a.sum, 171700);
    }

    @Test
    public void testAdderLMAX() throws Exception {
        Adder a = new Adder();
        new BatchReaderRange(1L, 100L).newThread().filter(l -> (l & 1) == 0L).map(l -> l * l).forEach(a).run();
//        Batch sequence = new BatchRange(1L, 100L).filter(l -> (l & 1) == 0L).map(l -> { System.out.println("Got " + l); return l * l; }).forEach(a);
        Assert.assertEquals(a.sum, 171700);
    }

    @Test
    public void testCounterDelays() throws Exception {
        Counter a = new Counter();
        Delay d = new Delay();
        Date start = new Date();
        new BatchReaderRange(1L, 10L).filter(d).filter(d).forEach(a).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 10);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    @Test
    public void testCounterDelaysParallel() throws Exception {
        Counter a = new Counter();
        Delay d = new Delay();
        Date start = new Date();
        new BatchReaderRange(1L, 10L).filter(d).newThread().filter(d).forEach(a).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 10);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

//    @Test
//    public void testCounterDelays4Parallel() throws Exception {
//        ParallelCounter a = new ParallelCounter();
//        Delay d = new Delay();
//        Date start = new Date();
//        new BatchReaderRange(1L, 12L).parallel(4).filter(d).forEach(a).run();
//        Date end = new Date();
//        Assert.assertEquals(a.num.get(), 12);
//        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
//    }
    @Test
    public void testCounterDelays3() throws Exception {
        Counter a = new Counter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).intfilter(d).intfilter(d).intfilter(d).forEach(a).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }
    // expect 7 seconds: 12 * 0.5 per stage = 6 seconds, + 2 * 0.5 for pipeline fill / drain
    @Test
    public void testCounterDelays3parallel() throws Exception {
        Counter a = new Counter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).intfilter(d).newThread().intfilter(d).newThread().intfilter(d).forEach(a).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    // expect 18 seconds
    @Test
    public void testCounterDelays3InConsumer() throws Exception {
        Counter a = new Counter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).intfilter(d).intfilter(d).forEach(new BatchWriterConsumer<Object>(a).intfilteredFrom(d)).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    // expect 7 seconds (18 / 3, plus 2 * 0.5 each for startup / shutdown
    @Test
    public void testCounterDelays3parallelInConsumer() throws Exception {
        Counter a = new Counter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).intfilter(d).newThread().intfilter(d).forEach(new BatchWriterConsumer<Object>(a).intfilteredFrom(d).newThread()).run();
        Date end = new Date();
        Assert.assertEquals(a.num, 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    // expect 1.5 seconds: 12 / 4 * 0.5
    @Test
    public void testCounterParallel() throws Exception {
        ParallelCounter ctr = new ParallelCounter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        BatchWriter<Object> consumer = new BatchWriterConsumer<Object>(ctr);
        BatchWriter<Object> consumer2 = consumer.intfilteredFrom(d);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).parallel(4, 16, new BatchWriterFactoryByIdentity<Object>(consumer2)).run();
        Date end = new Date();
        Assert.assertEquals(ctr.num.get(), 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }
    // the same, but with ArrayBlockingQueue (expect 1.5 seconds: 12 / 4 * 0.5
    @Test
    public void testCounterParallelViaQueue() throws Exception {
        ParallelCounter ctr = new ParallelCounter();
        BatchFilterDelay d = new BatchFilterDelay(500);
        BatchWriter<Object> consumer = new BatchWriterConsumer<Object>(ctr);
        BatchWriter<Object> consumer2 = consumer.intfilteredFrom(d);
        Date start = new Date();
        new Batches<Long>(new BatchReaderNewThreadsViaQueue<Long>(new BatchReaderRange(1L, 12L), 16, 4), new BatchWriterFactoryByIdentity<Object>(consumer2)).run();
        Date end = new Date();
        Assert.assertEquals(ctr.num.get(), 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    // split to different threads and collecting the results again!
    @Test
    public void testCounterParallelAndMerge() throws Exception {
        ParallelCounter ctr = new ParallelCounter();
        BatchWriter<Object> consumer = new BatchWriterConsumer<Object>(ctr);
        Date start = new Date();
//        new Batches<Long>(new BatchReaderNewThreadsViaQueue<Long>(new BatchReaderRange(1L, 12L), 16, 4),
//                new Collector<Long>(consumer)).run();
        new BatchReaderRange(1L, 12L).parallel(4, 16, new Collector<Long>(consumer)).run();

        Date end = new Date();
        Assert.assertEquals(ctr.num.get(), 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }

    // split to different threads and collecting the results again! With Delay! Expect 1.5 seconds
    @Test
    public void testCounterParallelAndMergeWithDelay() throws Exception {
        ParallelCounter ctr = new ParallelCounter();

        BatchProcessorFactory<Long, Long> justADelay = new BatchProcessorFactoryByIdentity<>(new DelayFunction<Long>());
        BatchWriter<Object> consumer = new BatchWriterConsumer<Object>(ctr);
        Date start = new Date();
        new BatchReaderRange(1L, 12L).parallel(4, 16, new Collector<Long>(consumer).mappedFrom(justADelay)).run();

        Date end = new Date();
        Assert.assertEquals(ctr.num.get(), 12);
        System.out.println("Took " + (end.getTime() - start.getTime()) + " ms");
    }


}
