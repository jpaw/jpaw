package de.jpaw8.batch.api;

import de.jpaw8.batch.consumers.impl.BatchWriterFactoryMapForProcessorFactory;

@FunctionalInterface
public interface BatchWriterFactory<E> extends BatchIO {
    BatchWriter<E> get(int threadno);

    // default methods to avoid the need for an abstract superclass of all implementations (in xtend, this would be just extension methods)
    // map

    default public <F> BatchWriterFactory<F> mappedFrom(BatchProcessorFactory<F,E> function) {
        return new BatchWriterFactoryMapForProcessorFactory<F,E>(this, function);
    }

}
