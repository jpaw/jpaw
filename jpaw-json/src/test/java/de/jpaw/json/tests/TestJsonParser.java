package de.jpaw.json.tests;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.json.JsonParser;

public class TestJsonParser {
    
    @Test
    public void testParseNumObject() throws Exception {
        Assert.assertEquals(new JsonParser("-65656565", true).parseElement(), Integer.valueOf(-65656565));
        Assert.assertEquals(new JsonParser("-656565628282828285", true).parseElement(), Long.valueOf(-656565628282828285L));
        Assert.assertEquals(new JsonParser("3.14", false).parseElement(), new BigDecimal("3.14"));
        Assert.assertEquals(new JsonParser("3.14", true).parseElement(), Double.valueOf(3.14));
    }
    
    @Test
    public void testParseStringObject() throws Exception {
        Assert.assertEquals(new JsonParser("\"hello\"", true).parseElement(), "hello");
        Assert.assertNull  (new JsonParser("null",      true).parseElement());
    }
    
    @Test
    public void testParseBooleanObject() throws Exception {
        Assert.assertEquals(new JsonParser("true",  true).parseElement(), Boolean.TRUE);
        Assert.assertEquals(new JsonParser("false", true).parseElement(), Boolean.FALSE);
    }
    
    @Test
    public void testParseMap() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hello", 42);
        map.put("what", true);
        map.put("why", "bla");
        
        String in = "  { \"what\"   : true, \"why\":\"bla\"   , \"hello\":  +42 } ";
        System.out.println("012345678901234567890123456789012345678901234567890123456789");
        System.out.println(in);
        Assert.assertEquals(new JsonParser(in, false).parseObject(), map);
    }
}
