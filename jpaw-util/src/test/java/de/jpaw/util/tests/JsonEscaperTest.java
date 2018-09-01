package de.jpaw.util.tests;

import org.junit.Assert;
import org.junit.Test;

import de.jpaw.json.BaseJsonComposer;


public class JsonEscaperTest {
    @Test
    public void testEscaping() throws Exception {
        StringBuilder buff = new StringBuilder(100);
        BaseJsonComposer escaper = new BaseJsonComposer(buff);
        escaper.outputUnicodeWithControls("E\nS\bC");
        Assert.assertEquals(buff.toString(), "\"E\\nS\\bC\"");
    }
}
