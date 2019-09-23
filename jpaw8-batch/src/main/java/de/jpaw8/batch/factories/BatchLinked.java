package de.jpaw8.batch.factories;

import de.jpaw8.batch.api.BatchIO;

/** A stream (reader) which receives input from a nested reader.
 * Purpose of this class is to invoke the open() and close() respectively.
 *
 */
public abstract class BatchLinked implements BatchIO {
    private final BatchIO myProducer;
    protected BatchLinked(BatchIO producer) {
        myProducer = producer;
    }

    @Override
    public void open() throws Exception {
        myProducer.open();
    }
    @Override
    public void close() throws Exception {
        myProducer.close();
    }

}
