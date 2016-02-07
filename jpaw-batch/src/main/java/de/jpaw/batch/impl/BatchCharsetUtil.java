package de.jpaw.batch.impl;

import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

public class BatchCharsetUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BatchCharsetUtil.class);
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");    // default character set is available on all platforms

    public static Charset charsetFromStringWithHelp(String charset) {
        if (charset != null) {
            if (charset.equals("LIST")) {
                // provide a list and terminate
                System.out
                        .println("Available charsets for this platform (and their aliases");
                for (Map.Entry<String, Charset> cs : Charset
                        .availableCharsets().entrySet()) {
                    System.out.println(cs.getKey() + ": "
                            + Joiner.on(", ").join(cs.getValue().aliases()));
                }
                System.exit(0);
            }
            // not help, want a specific one
            try {
                return Charset.forName(charset);
            } catch (Exception e) {
                LOG.error("Bad encoding name {} or encoding not available on this platform. Use LIST as a charset name to see available options");
                System.exit(1);
            }
        }
        return CHARSET_UTF8;
    }
}
