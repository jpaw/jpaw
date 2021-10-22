package de.jpaw.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRawTcpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRawTcpClient.class);

    protected final InetAddress addr;
    protected final Socket conn;
    protected final byte[] responseBuffer;

    private static void printSocketInfo(SSLSocket s) {
        LOGGER.info("Socket class: " + s.getClass());
        LOGGER.info("   Remote address = " + s.getInetAddress().toString());
        LOGGER.info("   Remote port = " + s.getPort());
        LOGGER.info("   Local socket address = " + s.getLocalSocketAddress().toString());
        LOGGER.info("   Local address = " + s.getLocalAddress().toString());
        LOGGER.info("   Local port = " + s.getLocalPort());
        LOGGER.info("   Need client authentication = " + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        LOGGER.info("   Cipher suite = " + ss.getCipherSuite());
        LOGGER.info("   Protocol = " + ss.getProtocol());
    }

    public SimpleRawTcpClient(String hostname, int port, boolean useSsl) throws IOException {
        this(hostname, port, useSsl, 64000);
    }

    public SimpleRawTcpClient(String hostname, int port, boolean useSsl, int bufferSize) throws IOException {
        addr = InetAddress.getByName(hostname);
        responseBuffer = new byte[bufferSize];

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
    }

    public byte[] doRawIO(byte[] request) throws Exception {
        boolean foundDelimiter = false;
        conn.getOutputStream().write(request, 0, request.length);
        int haveBytes = 0;
        do {
            int numBytes = conn.getInputStream().read(responseBuffer, haveBytes, responseBuffer.length - haveBytes);
            if (numBytes <= 0)
                break;
            for (int i = 0; i < numBytes; ++i) {
                if (responseBuffer[haveBytes + i] == (byte)0x0a) {
                    foundDelimiter = true;
                    break;
                    // fast track: return new ByteArrayParser(responseBuffer, 0, haveBytes+i+1).readRecord();
                }
            }
            haveBytes += numBytes;
        } while (!foundDelimiter);
        if (haveBytes <= 0)
            return null;
        return responseBuffer;
    }

    // close the connection
    public void close() throws IOException {
        conn.close();
    }

}
