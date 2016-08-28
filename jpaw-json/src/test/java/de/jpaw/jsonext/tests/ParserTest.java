package de.jpaw.jsonext.tests;

import org.testng.annotations.Test;

import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;


@Test
public class ParserTest {

    private void parseTest(String name) throws Exception {
        String json = Resources.readResource("input/" + name + ".json");
        System.out.println("JSON length for " + name + " is " + json.length());
        Object result = new JsonParser(json, true).parseObject();
    }

    public void parseTest1() throws Exception {
        parseTest("rap");
    }
    public void parseTest2() throws Exception {
        parseTest("caliper");
    }
    public void parseTest3() throws Exception {
        parseTest("test");
    }
    public void parseTest4() throws Exception {
        parseTest("numbers-array");
    }
    public void parseTest5() throws Exception {
        parseTest("long-string");
    }
}
