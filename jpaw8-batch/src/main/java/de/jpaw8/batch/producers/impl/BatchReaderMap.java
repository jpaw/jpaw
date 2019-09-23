package de.jpaw8.batch.producers.impl;

import java.util.function.Function;
import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;

public class BatchReaderMap<E,R> extends BatchLinked implements BatchReader<R> {
    private final BatchReader<? extends E> producer;
    private final Function<E,R> function;

    public BatchReaderMap(BatchReader<? extends E> producer, Function<E,R> function) {
        super(producer);
        this.producer = producer;
        this.function = function;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super R> whereToPut) throws Exception {
        producer.produceTo((data, i) -> whereToPut.accept(function.apply(data), i));
    }
}
