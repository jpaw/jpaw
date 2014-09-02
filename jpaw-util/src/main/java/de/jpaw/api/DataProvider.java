package de.jpaw.api;

import java.io.Serializable;

/** Describes a generic data provider, which can provide read-only access to some configuration, or perform R/W operations.
 * Implementations have to provide much less functionality than for a Map. 
 * 
 * @author Michael Bischoff
 *
 * @param <KEY>
 * @param <DATA>
 */
public interface DataProvider<KEY extends Serializable, DATA> {
    /** Returns the data for a given key, or null if it does not exist. */
    public DATA get(KEY key);
    
    /** Adds the provided data record as additional record, or updates an existing record, or removes it (if data is null).
     * May throw an UnsupportedOperationException if this is not supported by the provider.
     * 
     * @param data
     */
    public void set(KEY key, DATA data);
    
    /** Removes everything from the data store.
     * May throw an UnsupportedOperationException if this is not supported by the provider.
     */
    public void clear();
    
    /** Initializes some cache from disk, if required. Does nothing if caching is not supported. */
    public void init();
}
