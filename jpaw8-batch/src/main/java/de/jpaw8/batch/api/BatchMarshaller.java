package de.jpaw8.batch.api;

import java.io.OutputStream;

/** Bidirectional conversion between byte [] and a type X, for use in the rest or socket remoter. */
public interface BatchMarshaller<X> {
    String getContentType();
    default byte getDelimiter() { return (byte)'\n'; }              // returns the character which ends a message

    byte [] marshal(X request) throws Exception;                    // may be slow due to coyping

    // preferred method (overwriting offers a chance to avoid buffer copies)
    default void marshal(X request, OutputStream w) throws Exception {
        w.write(marshal(request));
    }

    X unmarshal(byte [] response, int length) throws Exception;     // may be slow due to coyping
}
