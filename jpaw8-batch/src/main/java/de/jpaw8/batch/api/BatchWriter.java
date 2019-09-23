package de.jpaw8.batch.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import de.jpaw8.batch.consumers.impl.BatchWriterConsumer;
import de.jpaw8.batch.consumers.impl.BatchWriterFilter;
import de.jpaw8.batch.consumers.impl.BatchWriterFilterInt;
import de.jpaw8.batch.consumers.impl.BatchWriterFilterObjInt;
import de.jpaw8.batch.consumers.impl.BatchWriterMap;
import de.jpaw8.batch.consumers.impl.BatchWriterMapForProcessor;
import de.jpaw8.batch.consumers.impl.BatchWriterMapObjInt;
import de.jpaw8.batch.consumers.impl.BatchWriterNewThread;
import de.jpaw8.function.ObjIntFunction;
import de.jpaw8.function.ObjIntPredicate;

/** Defines the methods a jpaw batch output writer must implement.
 * The accept() method is called in ordered or unordered sequence for every processed record.
 * If processing resulted in an exception, the data component of response will be null.
 * A single thread (or the main thread) will be allocated to writing.
 *
 * This interface loosely corresponds to the Java 8 ObjIntConsumer<F> interface, but allows exceptions.
 * Linking is done via the BatchWriterLinked class.
 */

@FunctionalInterface
public interface BatchWriter<E> extends BatchIO {
    void store(E response, int no);

    public static <X> BatchWriter<X> of(Consumer<X> consumer) {
        return new BatchWriterConsumer<X>(consumer);
    }

    // filter

    default public BatchWriter<E> filteredFrom(Predicate<? super E> filter) {
        return new BatchWriterFilter<E>(this, filter);
    }
    default public BatchWriter<E> intfilteredFrom(IntPredicate ordinalFilter) {       // different name required because it's ambiguous otherwise
        return new BatchWriterFilterInt<E>(this, ordinalFilter);
    }
    default public BatchWriter<E> filteredFrom(ObjIntPredicate<? super E> biFilter) {
        return new BatchWriterFilterObjInt<E>(this, biFilter);
    }



    // map

    default public <F> BatchWriter<F> mappedFrom(Function<F,E> function) {
        return new BatchWriterMap<F,E>(this, function);
    }
    default public <F> BatchWriter<F> mappedFrom(ObjIntFunction<F,E> function) {
        return new BatchWriterMapObjInt<F,E>(this, function);
    }
    default public <F> BatchWriter<F> mappedFrom(BatchProcessor<F,E> function) {
        return new BatchWriterMapForProcessor<F,E>(this, function);
    }



    // thread splitter

    default public <F> BatchWriter<E> newThread() {
        return new BatchWriterNewThread<E>(this, 1024);
    }
    default public <F> BatchWriter<E> parallel(int numThreads) {
        return new BatchWriterNewThread<E>(this, 1024, numThreads);
    }

}
