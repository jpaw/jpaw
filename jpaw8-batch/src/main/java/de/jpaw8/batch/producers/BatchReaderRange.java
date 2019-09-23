package de.jpaw8.batch.producers;

import java.util.function.ObjIntConsumer;

import de.jpaw8.batch.api.BatchReader;

public class BatchReaderRange implements BatchReader<Long> {
    final long from, to;

    public BatchReaderRange(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void produceTo(ObjIntConsumer<? super Long> whereToPut) throws Exception {
        int n = 0;
        for (long l = from; l <= to; ++l)
            whereToPut.accept(Long.valueOf(l), ++n);
    }
}
