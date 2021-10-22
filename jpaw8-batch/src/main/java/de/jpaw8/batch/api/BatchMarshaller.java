package de.jpaw8.batch.api;

import java.io.OutputStream;

/** Bidirectional conversion between byte[] and a type X, for use in the rest or socket remoter. */
public interface BatchMarshaller<X> {
    String getContentType();

    /** Returns the character which ends a message. */
    default byte getDelimiter() {
        return (byte)'\n';
    }

    byte[] marshal(X request) throws Exception;                    // may be slow due to copying

    // preferred method (overwriting offers a chance to avoid buffer copies)
    default void marshal(X request, OutputStream w) throws Exception {
        w.write(marshal(request));
    }

    X unmarshal(byte[] response, int length) throws Exception;     // may be slow due to copying
}
