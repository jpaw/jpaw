package de.jpaw.util.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.util.UnsafeUtil;

public class UnsafeTest {
    @Test
    public void testUnsafe() {
        String testString = "JDK6";

        char [] array = UnsafeUtil.getStringBuffer(testString);
        Assert.assertEquals(array.length, 4);

        array[3] = '7';

        System.out.println("I'm at least on " + testString);
    }
}
