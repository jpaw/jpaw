package de.jpaw.json.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.json.ExtendedJsonEscaperForAppendables;
import de.jpaw.json.JsonParser;
import de.jpaw.util.JsonEscaper;

public class TestJsonEscapes {
    
    @Test
    public void testEscapeJsonString() throws Exception {
        String input = "hello \"world\"\r\n  \1 ctrl-A \\";
        String output = "\"hello \\\"world\\\"\\r\\n  \\u0001 ctrl-A \\\\\"";
        
        StringBuilder sb = new StringBuilder();
        JsonEscaper out = new ExtendedJsonEscaperForAppendables(sb);
        out.outputUnicodeWithControls(input);
        Assert.assertEquals(sb.toString(), output);
        
        JsonParser jp = new JsonParser(output, false);
        Object r = jp.parseElement();
        Assert.assertEquals(r, input);
    }
}
