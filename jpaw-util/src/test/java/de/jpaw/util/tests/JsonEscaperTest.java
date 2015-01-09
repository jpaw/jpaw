package de.jpaw.util.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.util.DefaultJsonEscaperForAppendables;


public class JsonEscaperTest {
    @Test
    public void testEscaping() throws Exception {
        StringBuilder buff = new StringBuilder(100);
        DefaultJsonEscaperForAppendables escaper = new DefaultJsonEscaperForAppendables(buff);
        escaper.outputUnicodeWithControls("E\nS\bC");
        Assert.assertEquals(buff.toString(), "\"E\\nS\\bC\"");
    }
}
