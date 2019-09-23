package de.jpaw8.batch.api;

@FunctionalInterface
public interface BatchMarshallerFactory<X> {
    BatchMarshaller<X> getMarshaller(int threadNo);
}
