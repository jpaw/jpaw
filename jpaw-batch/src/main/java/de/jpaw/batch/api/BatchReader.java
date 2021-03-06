package de.jpaw.batch.api;


/** Defines the methods a bonaparte batch processor must implement.
 * The implementation typically also hosts the main() method, and invokes the batch processor
 * with a reference of an instance to itself.
 *
 * This interface loosely corresponds to the Java 8 streams
 */

public interface BatchReader<E> extends Contributor {
    public void produceTo(BatchMainCallback<? super E> whereToPut) throws Exception;
}
