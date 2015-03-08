package de.jpaw.batch.api;

/** Callback which an input source has to call per record.
 *
 * Corresponds to a Consumer in the Java 8 streams API.
 */
public interface BatchMainCallback<E> {
    void accept(E record);
}
