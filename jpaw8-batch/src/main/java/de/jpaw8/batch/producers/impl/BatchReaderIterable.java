package de.jpaw8.batch.producers.impl;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;

public class BatchReaderIterable<E> implements BatchReader<E> {
    private final Iterable<E> iter;

    public BatchReaderIterable(Iterable<E> iter) {
        this.iter = iter;
    }

    @Override
    public void produceTo(ObjIntConsumer<? super E> whereToPut) throws Exception {
        int n = 0;
        for (E e : iter)
            whereToPut.accept(e, ++n);
    }
}
