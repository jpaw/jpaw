package de.jpaw.util.tests;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.util.Escape2Java;

public class Escape2JavaTest {
    private static final int N = 0x7e;

    @Test
    public void compareJavaEscapersTest() throws Exception {
        for (int i = 0; i < N; ++i) {
            String s = String.valueOf((char)i);
            String esc1 = StringEscapeUtils.escapeJava(s);
            String esc2 = Escape2Java.escapeString2Java(s);

            Assertions.assertEquals(esc2, esc1);
        }
    }
}
