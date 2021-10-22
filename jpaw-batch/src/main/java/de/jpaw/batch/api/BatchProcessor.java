package de.jpaw.batch.api;

/** Process input of type E to produce output of type F.
 *
 *  To be used like Java 8 Function, in map[]. */
public interface BatchProcessor<E, F> {
    F process(int recordNo, E data) throws Exception;
    void close() throws Exception;
}
