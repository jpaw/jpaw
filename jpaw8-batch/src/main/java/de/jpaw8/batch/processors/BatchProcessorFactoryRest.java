package de.jpaw8.batch.processors;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineCallback;
import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.api.BatchMarshaller;
import de.jpaw8.batch.api.BatchMarshallerFactory;
import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;

public class BatchProcessorFactoryRest<X> implements BatchProcessorFactory<X,X>, CmdlineCallback {
    private final BatchMarshallerFactory<X> marshallerFactory;
    private final BatchMarshaller<X> immutableMarshaller;
    private int bufferSize = 1024 * 1024;
    private URL url = null;

    private BatchProcessorFactoryRest(BatchMarshallerFactory<X> marshallerFactory, BatchMarshaller<X> immutableMarshaller) {
        this.immutableMarshaller = immutableMarshaller;
        this.marshallerFactory = marshallerFactory;
        CmdlineParserContext.getContext()
            .addFlaggedOption("url", JSAP.URL_PARSER, null, JSAP.REQUIRED, 'U', "remote URL")
            .addFlaggedOption("buffersize", JSAP.INTEGER_PARSER, "1000000", JSAP.NOT_REQUIRED, 'B', "buffer size for REST requests")
            .registerCallback(this);
    }

    public BatchProcessorFactoryRest(BatchMarshallerFactory<X> marshallerFactory) {
        this(marshallerFactory, null);
    }
    public BatchProcessorFactoryRest(BatchMarshaller<X> immutableMarshaller) {
        this(null, immutableMarshaller);
    }

    /** Programmatic creation. */
    public BatchProcessorFactoryRest(BatchMarshallerFactory<X> marshallerFactory, int bufferSize, URL url) {
        this.marshallerFactory = marshallerFactory;
        this.immutableMarshaller = null;
        this.bufferSize = bufferSize;
        this.url = url;
    }
    public BatchProcessorFactoryRest(BatchMarshaller<X> immutableMarshaller, int bufferSize, URL url) {
        this.marshallerFactory = null;
        this.immutableMarshaller = immutableMarshaller;
        this.bufferSize = bufferSize;
        this.url = url;
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        bufferSize = ctx.getInt("buffersize");
        url = ctx.getURL("url");
    }

    @Override
    public BatchProcessor<X,X> getProcessor(int threadNo) {
        return new BatchProcessorRest<X>(bufferSize, url,
                immutableMarshaller != null ? immutableMarshaller : marshallerFactory.getMarshaller(threadNo));
    }

    private static class BatchProcessorRest<X> implements BatchProcessor<X,X> {
        private final byte [] buffer;
        private final URL url;
        private final BatchMarshaller<X> marshaller;

        private BatchProcessorRest(int bufferSize, URL url, BatchMarshaller<X> marshaller) {
            buffer = new byte [bufferSize];
            this.url = url;
            this.marshaller = marshaller;
        }

        @Override
        public X process(X data, int recordNo) throws Exception {
            // get the raw data
            byte [] payload = marshaller.marshal(data);

            // 1.) create a connection to the target. This does not use any of the above SSL context.

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", marshaller.getContentType());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + payload.length);
            connection.setUseCaches(false);

            int length = 0;
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {

                wr.write(payload);
                // marshaller.marshal(data, connection.getOutputStream()); // unfortunately we need the length

                wr.flush();

                // 4.) retrieve the response as required
                try (InputStream inputstream = connection.getInputStream()) {
                    int morebytes;
                    while ((morebytes = inputstream.read(buffer, length, buffer.length - length)) > 0) {
                        length += morebytes;
                    }
                }
            }

            return marshaller.unmarshal(buffer, length);
        }
    }

}
