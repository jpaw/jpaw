package de.jpaw.json.tests;

import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.json.JsonParser;

public class TestJsonListParser {
    static final class EmitChecker implements Consumer<Map<String, Object>> {
        private final int[] expected;
        private int index = 0;

        private EmitChecker(int[] expected) {
            this.expected = expected;
        }

        @Override
        public void accept(Map<String, Object> map) {
            if (index >= expected.length)
                throw new RuntimeException("Too many objects emitted");
            if (map == null)
                Assertions.assertEquals(expected[index], -1, "Expected a non-null object");
            else
                Assertions.assertEquals(expected[index], map.size(), "Number of parsed elements");
            ++index;
        }

        public void ensureAllUsed() {
            Assertions.assertEquals(expected.length, index, "Expected more elements emitted");
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
