package de.jpaw8.batch.processors;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineCallback;
import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw.socket.SessionInfo;
import de.jpaw8.batch.api.BatchMarshaller;
import de.jpaw8.batch.api.BatchMarshallerFactory;
import de.jpaw8.batch.api.BatchProcessor;
import de.jpaw8.batch.api.BatchProcessorFactory;

public class BatchProcessorFactoryTcp<X> implements BatchProcessorFactory<X,X>, CmdlineCallback {
    private static final Logger LOG = LoggerFactory.getLogger(BatchProcessorFactoryTcp.class);
    private final BatchMarshallerFactory<X> marshallerFactory;
    private final BatchMarshaller<X> immutableMarshaller;
    private int bufferSize = 1024 * 1024;
    private int port = 80;
    private boolean useSsl = false;
    private InetAddress addr;

    private BatchProcessorFactoryTcp(BatchMarshallerFactory<X> marshallerFactory, BatchMarshaller<X> immutableMarshaller) {
        this.immutableMarshaller = immutableMarshaller;
        this.marshallerFactory = marshallerFactory;
        CmdlineParserContext.getContext()
            .addFlaggedOption("host", JSAP.INETADDRESS_PARSER, "localhost", JSAP.NOT_REQUIRED, 'H', "remote host name or IP address")
            .addFlaggedOption("port", JSAP.INTEGER_PARSER, "80", JSAP.NOT_REQUIRED, 'P', "server TCP/IP port")
            .addSwitch("ssl", 'S', "use SSL")
            .addFlaggedOption("buffersize", JSAP.INTEGER_PARSER, "1000000", JSAP.NOT_REQUIRED, 'B', "buffer size for REST requests")
            .registerCallback(this);
    }
    public BatchProcessorFactoryTcp(BatchMarshallerFactory<X> marshallerFactory) {
        this(marshallerFactory, null);
    }
    public BatchProcessorFactoryTcp(BatchMarshaller<X> immutableMarshaller) {
        this(null, immutableMarshaller);
    }

    /** Programatic constructor. */
    public BatchProcessorFactoryTcp(BatchMarshallerFactory<X> marshallerFactory, BatchMarshaller<X> immutableMarshaller,
            InetAddress addr, int port, boolean useSsl, int buffersize) {
        this.immutableMarshaller = immutableMarshaller;
        this.marshallerFactory = marshallerFactory;
        this.addr = addr;
        this.port = port;
        this.useSsl = useSsl;
        this.bufferSize = buffersize;
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        bufferSize = ctx.getInt("buffersize");
        port = ctx.getInt("port");
        useSsl = ctx.getBoolean("ssl");
        addr = ctx.getInetAddress("host");
    }

    private static void printSocketInfo(SSLSocket s) {
        LOG.info("Socket class: " + s.getClass());
        LOG.info("   Remote address = " + s.getInetAddress().toString());
        LOG.info("   Remote port = " + s.getPort());
        LOG.info("   Local socket address = " + s.getLocalSocketAddress().toString());
        LOG.info("   Local address = " + s.getLocalAddress().toString());
        LOG.info("   Local port = " + s.getLocalPort());
        LOG.info("   Need client authentication = " + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        LOG.info("   Cipher suite = " + ss.getCipherSuite());
        LOG.info("   Protocol = " + ss.getProtocol());
    }

    @Override
    public BatchProcessor<X,X> getProcessor(int threadNo) throws IOException {
        // connect and then return the new processor
        Socket conn = null;
        if (useSsl) {
            SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
            conn = f.createSocket(addr, port);
            SSLSocket c = (SSLSocket) conn;
            printSocketInfo(c);
            c.startHandshake();
            SSLSession session = c.getSession();
            SessionInfo.logSessionInfo(session, "Server");
        } else {
            conn = new Socket(addr, port);
        }
        return new BatchProcessorTcp<X>(bufferSize, conn,
                immutableMarshaller != null ? immutableMarshaller : marshallerFactory.getMarshaller(threadNo));
    }

    private static class BatchProcessorTcp<X> implements BatchProcessor<X,X> {
        private final Socket conn;
        private final byte [] responseBuffer;
        private final BatchMarshaller<X> marshaller;

        private BatchProcessorTcp(int bufferSize, Socket conn, BatchMarshaller<X> marshaller) {
            responseBuffer = new byte [bufferSize];
            this.marshaller = marshaller;
            this.conn = conn;
        }

        @Override
        public X process(X data, int recordNo) throws Exception {
            // get the raw data
            boolean foundDelimiter = false;
            byte delimiter = marshaller.getDelimiter();

//          byte [] payload = marshaller.marshal(data);
//          conn.getOutputStream().write(payload);
            marshaller.marshal(data, conn.getOutputStream());
            int haveBytes = 0;
            do {
                int numBytes = conn.getInputStream().read(responseBuffer, haveBytes, responseBuffer.length - haveBytes);
                if (numBytes <= 0)
                    break;
                for (int i = 0; i < numBytes; ++i) {
                    if (responseBuffer[haveBytes+i] == delimiter) {
                        foundDelimiter = true;
                        break;
                        // fast track: return new ByteArrayParser(responseBuffer, 0, haveBytes+i+1).readRecord();
                    }
                }
                haveBytes += numBytes;
            } while (!foundDelimiter);
            if (haveBytes <= 0)
                return null;

            return marshaller.unmarshal(responseBuffer, haveBytes);
        }
    }

}
