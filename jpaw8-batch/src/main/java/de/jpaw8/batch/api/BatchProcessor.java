package de.jpaw8.batch.api;

/** Process input of type E to produce output of type F.
 *
 *  To be used like Java 8 ObjIntFunction, but allows for exceptions, and has a close method. */
@FunctionalInterface
public interface BatchProcessor<E,F> {
    F process(E data, int recordNo) throws Exception;
    default void close() throws Exception {}
}
