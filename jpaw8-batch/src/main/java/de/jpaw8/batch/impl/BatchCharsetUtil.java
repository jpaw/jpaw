package de.jpaw8.batch.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchCharsetUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BatchCharsetUtil.class);

    public static Charset charsetFromStringWithHelp(String charset) {
        if (charset != null) {
            if (charset.equals("LIST")) {
                // provide a list and terminate
                System.out
                        .println("Available charsets for this platform (and their aliases");
                for (Map.Entry<String, Charset> cs : Charset
                        .availableCharsets().entrySet()) {
                    StringJoiner j = new StringJoiner(", ");
                    cs.getValue().aliases().stream().forEach(s -> j.add(s));
                    System.out.println(cs.getKey() + ": " + j);
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
        return StandardCharsets.UTF_8;
    }
}
