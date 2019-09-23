package de.jpaw8.batch.lmax;

import java.util.function.ObjIntConsumer;

import com.lmax.disruptor.RingBuffer;

/** Data needed by newThread as well as parallel() implementations of the disruptor. */
public class TheConsumer<F> implements ObjIntConsumer<F> {
    private final RingBuffer<DataWithOrdinal<F>> rb;

    public TheConsumer(RingBuffer<DataWithOrdinal<F>> rb) {
        this.rb = rb;
    }

    // BatchMainCallback. Calls to this procedure feed the input disruptor
    @Override
    public void accept(F record, int n) {

        long sequence = rb.next(); // Grab the next sequence
        try {
            DataWithOrdinal<F> event = rb.get(sequence); // Get the entry in the Disruptor for the sequence
            event.data = record; // fill data
            event.recordno = n;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            rb.publish(sequence);
        }
    }

}
