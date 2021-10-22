package de.jpaw.util.tests;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import de.jpaw.util.ByteArray;

public class ByteArrayTest {

    @Test
    public void testByteArrayToOutputStream() throws Exception {
        final String hello = "Hello, world!";
        final byte[] data = hello.getBytes("UTF-8");
        final ByteArray ba = new ByteArray(data);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ba.toOutputStream(baos);
        baos.flush();
        Assert.assertArrayEquals(data, ba.getBytes());
        Assert.assertArrayEquals(data, baos.toByteArray());
    }
}
