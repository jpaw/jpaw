package de.jpaw.json.tests;

import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import de.jpaw.json.JsonParser;

public class TestJsonListParser {
    static class EmitChecker implements Consumer<Map<String, Object>> {
        private final int [] expected;
        private int index = 0;

        private EmitChecker(int [] expected) {
            this.expected = expected;
        }

        @Override
        public void accept(Map<String, Object> map) {
            if (index >= expected.length)
                throw new RuntimeException("Too many objects emitted");
            if (map == null)
                Assert.assertEquals("Expected a non-null object", expected[index], -1);
            else
                Assert.assertEquals("Number of parsed elements", expected[index], map.size());
            ++index;
        }

        public void ensureAllUsed() {
            Assert.assertEquals("Expected more elements emitted", expected.length, index);
        }
    }

    private void testRunner(String json, int... numElements) {
        JsonParser p = new JsonParser(json, true);
        EmitChecker checker = new EmitChecker(numElements);
        p.parseObjectOrListOfObjects(checker);
        checker.ensureAllUsed();
    }

    @Test
    public void testEmptyFile() throws Exception {
        testRunner("");
    }
    @Test
    public void testEmptyList() throws Exception {
        testRunner(" [ ]   ");
    }
    @Test
    public void testSingleNull() throws Exception {
        testRunner(" null   ", -1);
    }
    @Test
    public void testListNull() throws Exception {
        testRunner(" [ null ]  ", -1);
    }
    @Test
    public void testSingleObjectNoElements() throws Exception {
        testRunner(" {   } ", 0);
    }
    @Test
    public void testSingleObjectWithElements() throws Exception {
        testRunner(" { \"hello\": true  } ", 1);
    }
    @Test
    public void testListObjectWithElements() throws Exception {
        testRunner(" [ { \"hello\": true  }, null ] ", 1, -1);
    }
}
