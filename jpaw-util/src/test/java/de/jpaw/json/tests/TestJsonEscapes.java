package de.jpaw.json.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.json.BaseJsonComposer;
import de.jpaw.json.JsonEscaper;
import de.jpaw.json.JsonParser;

public class TestJsonEscapes {

    @Test
    public void testEscapeJsonString() throws Exception {
        String input = "hello \"world\"\r\n  \1 ctrl-A \\";
        String output = "\"hello \\\"world\\\"\\r\\n  \\u0001 ctrl-A \\\\\"";

        StringBuilder sb = new StringBuilder();
        JsonEscaper out = new BaseJsonComposer(sb);
        out.outputUnicodeWithControls(input);
        Assertions.assertEquals(output, sb.toString());

        JsonParser jp = new JsonParser(output, false);
        Object r = jp.parseElement();
        Assertions.assertEquals(input, r);
    }
}
