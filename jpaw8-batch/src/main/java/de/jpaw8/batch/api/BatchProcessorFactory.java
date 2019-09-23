package de.jpaw8.batch.api;

import de.jpaw8.batch.processors.BatchProcessorFactoryRest;
import de.jpaw8.batch.processors.BatchProcessorFactoryTcp;


/** Process input of type E to produce output of type F. */
@FunctionalInterface
public interface BatchProcessorFactory<E,F> extends BatchIO {
    BatchProcessor<E,F> getProcessor(int threadNo) throws Exception;

    public static final String SYSTEM_PROPERTY_CONNECTION = System.getProperty("jpaw.remote.connection", "rest");

    public static <X> BatchProcessorFactory<X, X> getRemoteBatchProcessorFactoryBySystemProperty(BatchMarshallerFactory<X> marshallerFactory) {
        switch (SYSTEM_PROPERTY_CONNECTION) {
        case "rest":
            return new BatchProcessorFactoryRest<X>(marshallerFactory);
        case "socket":
            return new BatchProcessorFactoryTcp<X>(marshallerFactory);
        default:
            System.err.println("Unknown parameter to system property jpaw.remote.connection: must define either rest or socket, got " + SYSTEM_PROPERTY_CONNECTION);
            System.exit(1);
            return null;  // should not happen!
        }
    }

    public static <X> BatchProcessorFactory<X, X> getRemoteBatchProcessorFactoryBySystemProperty(BatchMarshaller<X> immutableMarshaller) {
        switch (SYSTEM_PROPERTY_CONNECTION) {
        case "rest":
            return new BatchProcessorFactoryRest<X>(immutableMarshaller);
        case "socket":
            return new BatchProcessorFactoryTcp<X>(immutableMarshaller);
        default:
            System.err.println("Unknown parameter to system property jpaw.remote.connection: must define either rest or socket, got " + SYSTEM_PROPERTY_CONNECTION);
            System.exit(1);
            return null;  // should not happen!
        }
    }

}
