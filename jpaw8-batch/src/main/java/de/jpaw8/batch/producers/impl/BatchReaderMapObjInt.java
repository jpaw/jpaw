package de.jpaw8.batch.producers.impl;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;
import de.jpaw8.batch.factories.BatchLinked;
import de.jpaw8.function.ObjIntFunction;

public class BatchReaderMapObjInt<E,R> extends BatchLinked implements BatchReader<R> {
    private final BatchReader<? extends E> producer;
    private final ObjIntFunction<E,R> function;

    public BatchReaderMapObjInt(BatchReader<? extends E> producer, ObjIntFunction<E,R> function) {
        super(producer);
        this.producer = producer;
        this.function = function;
    }

    @Override
    public void produceTo(final ObjIntConsumer<? super R> whereToPut) throws Exception {
        producer.produceTo((data, i) -> whereToPut.accept(function.apply(data, i), i));
    }
}
