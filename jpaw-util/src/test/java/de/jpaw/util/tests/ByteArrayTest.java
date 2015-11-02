package de.jpaw.util.tests;

import java.io.ByteArrayOutputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.util.ByteArray;

public class ByteArrayTest {
    
    @Test
    public void testByteArrayToOutputStream() throws Exception {
        final String hello = "Hello, world!";
        final byte [] data = hello.getBytes("UTF-8");
        final ByteArray ba = new ByteArray(data);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ba.toOutputStream(baos);
        baos.flush();
        Assert.assertEquals(data, ba.getBytes());
        Assert.assertEquals(data, baos.toByteArray());
    }
}
