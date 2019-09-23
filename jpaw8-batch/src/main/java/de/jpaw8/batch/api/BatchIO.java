package de.jpaw8.batch.api;

public interface BatchIO {
    default void open() throws Exception {}
    default void close() throws Exception {}
}
