package de.jpaw.util.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.jpaw.util.MapIterator;

public class MapIteratorTest {
    @Test
    public void testMapIterator() {
        Map<String, Object> testmap = new HashMap<String, Object>();
        testmap.put("foo", "bar");
        MapIterator<?> me = new MapIterator(testmap);
        while (me.hasNext()) {
            System.out.println("next value is " + me.next().toString());
        }
    }
}
