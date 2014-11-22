package de.jpaw.batch.api;

/** Callback which an input source has to call per record.
 * 
 */
public interface BatchMainCallback<E> {
    void scheduleForProcessing(E record);
}
