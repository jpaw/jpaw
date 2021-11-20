package de.jpaw.util.tests;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.util.Cesu8Encoder;

public class UTF8EncoderTest {

    @Test
    public void testEncoder() throws UnsupportedEncodingException {
        // for the range tested, CESU8 is identical to UTF8
        for (int i = 0; i < 0xd800; ++i) {
            String testString = "A" + Character.valueOf((char)i) + "O";
            byte[] fromString = testString.getBytes("UTF-8");
            byte[] myOwn = Cesu8Encoder.encodeToCesu8(testString);
            Assertions.assertArrayEquals(fromString, myOwn, "for index " + i);
        }
    }

//  commented out, CESU8 direct was experimental
//    @Test
//    public void testUnsafeEncoder() throws UnsupportedEncodingException {
//        for (int i = 0; i < 0xd800; ++i) {
//            String testString = "A" + Character.valueOf((char)i) + "O";
//            byte[] fromString = testString.getBytes("UTF-8");
//            byte[] myOwn = Cesu8DirectEncoder.encodeToCesu8(testString);
//            Assertions.assertEquals(fromString, myOwn, "for index " + i);
//        }
//    }
//    @Test
//    public void testEncoder1() throws UnsupportedEncodingException {
//        String testString = "A" + Character.valueOf((char) 55296) + "O";
//        byte[] fromString = testString.getBytes("UTF-8");
//        byte[] myOwn = UTF8Encoder.encodeToCESU8(testString);
//        Assertions.assertEquals(fromString, myOwn);
//    }
}
