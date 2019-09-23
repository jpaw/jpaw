package de.jpaw8.batch.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;

import de.jpaw8.batch.consumers.BatchWriterDevNull;
import de.jpaw8.batch.consumers.impl.BatchWriterConsumer;
import de.jpaw8.batch.consumers.impl.BatchWriterConsumerObjInt;
import de.jpaw8.batch.producers.impl.BatchReaderFilter;
import de.jpaw8.batch.producers.impl.BatchReaderFilterInt;
import de.jpaw8.batch.producers.impl.BatchReaderFilterObjInt;
import de.jpaw8.batch.producers.impl.BatchReaderIterable;
import de.jpaw8.batch.producers.impl.BatchReaderMap;
import de.jpaw8.batch.producers.impl.BatchReaderMapObjInt;
import de.jpaw8.batch.producers.impl.BatchReaderNewThread;
import de.jpaw8.batch.producers.impl.BatchReaderNewThreads;
import de.jpaw8.function.ObjIntFunction;
import de.jpaw8.function.ObjIntPredicate;


/** Defines the methods a jpaw batch processor record source must implement, typically via subclassing BatchStream.
 * Pipelining of processing steps is possible by linking functional interfaces, using subclasses of the BatchLinkedStream class.
 *
 * Implementing classes work similar to the Java 8 streams, but using a push method instead of the Java 8 streams / Iterable pulling via getNext().
 */

@FunctionalInterface
public interface BatchReader<E> extends BatchIO {
    // the only real method
    public void produceTo(ObjIntConsumer<? super E> whereToPut) throws Exception;

    /** Creates a batch from some Iterable. */
    static public <F> BatchReaderIterable<F> of(Iterable<F> iter) {
        return new BatchReaderIterable<F>(iter);
    }



    // filter

    default public BatchReader<E> filter(Predicate<? super E> filter) {
        return new BatchReaderFilter<E>(this, filter);
    }
    default public BatchReader<E> intfilter(IntPredicate ordinalFilter) {       // different name required because it's ambiguous otherwise
        return new BatchReaderFilterInt<E>(this, ordinalFilter);
    }
    default public BatchReader<E> filter(ObjIntPredicate<? super E> biFilter) {
        return new BatchReaderFilterObjInt<E>(this, biFilter);
    }



    // map

    default public <F> BatchReader<F> map(Function<E,F> function) {
        return new BatchReaderMap<E,F>(this, function);
    }
    default public <F> BatchReader<F> map(ObjIntFunction<E,F> function) {
        return new BatchReaderMapObjInt<E,F>(this, function);
    }



    // forEach

    default public Batch<E> forEach(Consumer<? super E> consumer) {
        return new Batch<E> (this, new BatchWriterConsumer<E>(consumer));
    }
    default public Batch<E> forEach(ObjIntConsumer<? super E> consumer) {
        return new Batch<E> (this, new BatchWriterConsumerObjInt<E>(consumer));
    }
    default public Batch<E> forEach(BatchWriter<? super E> consumer) {
        return new Batch<E> (this, consumer);
    }

    // discard
    default public Batch<E> discard() {
        return new Batch<E> (this, new BatchWriterDevNull<E>());
    }
    // add a discard and run it right away.
    default public void run() throws Exception {
        discard().run();       // just a synonym
    }



    // thread splitter

    default public <F> BatchReader<E> newThread() {
        return new BatchReaderNewThread<E>(this, 1024);
    }

    // execute parallel sinks (corresponds to forEach)
    default public Batches<E> parallel(BatchWriterFactory<? super E> writers) {
        return new Batches<E>(new BatchReaderNewThreads<E>(this), writers);
    }
    default public Batches<E> parallel(int numThreads, int bufferSize, BatchWriterFactory<? super E> writers) {
        return new Batches<E>(new BatchReaderNewThreads<E>(this, bufferSize, numThreads), writers);
    }
    // parallel sinks with an arbitrary splitter
    // not used, it's static!
//    public Batches<E> parallel(BatchReaderFactory<? extends E> readers, BatchWriterFactory<? super E> writers) {
//        return new Batches<E>(readers, writers);
//    }
}
