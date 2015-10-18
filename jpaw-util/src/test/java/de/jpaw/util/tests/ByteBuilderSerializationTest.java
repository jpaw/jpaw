package de.jpaw.util.tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.util.ByteBuilder;

// various tests to compare ByteBuilder with DataOutput as implemented by ByteOutputStream
public class ByteBuilderSerializationTest {
    
    private static class Engine {
        final ByteBuilder bb = new ByteBuilder();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        
        // compare the serialized forms of both methods
        void compare(String msg) throws Exception {
            byte [] data_bb = bb.getBytes();
            byte [] data_dos = baos.toByteArray();
            System.out.println("Length for test " + msg + " is " + data_bb.length + " bytes (should be " + data_dos.length + " bytes)");
            Assert.assertEquals(data_bb, data_dos);
        }
    }
    
    // construct a string of some characters of all possible UTF lengths
    private String getString() {
        int i = 0;
        int data = 0;
        char [] raw = new char[50];
        while (i < raw.length) {
            raw[i] = (char)data;
            data = data * 5 + 1;
            ++i;
            if (data > 0xd000)      // end of BMP
                break;
        }
        return new String(raw, 0, i);
    }
    
    @Test
    public void testStrings() throws Exception {
        System.out.println("Test string has " + getString().length() + " characters");
    }

    @Test
    public void testBytes() throws Exception {
        String test = getString();
        Engine e = new Engine();
        e.bb.writeBytes(test);
        e.dos.writeBytes(test);
        e.compare("Bytes");
    }

    @Test
    public void testChars() throws Exception {
        String test = getString();
        Engine e = new Engine();
        e.bb.writeChars(test);
        e.dos.writeChars(test);
        e.compare("Chars");
    }

    @Test
    public void testUTF() throws Exception {
        String test = getString();
        Engine e = new Engine();
        e.bb.writeUTF(test);
        e.dos.writeUTF(test);
        e.compare("UTF");
    }

    
    
    // construct a string of some characters of all possible UTF lengths
    private byte [] getByteArray() {
        int i = 0;
        int data = 0;
        byte [] raw = new byte[50];
        while (i < raw.length) {
            raw[i] = (byte)data;
            data = data * 5 + 1;
            ++i;
        }
        return raw;
    }
    
    @Test
    public void testByteArray() throws Exception {
        byte [] test = getByteArray();
        Engine e = new Engine();
        e.bb.write(test);
        e.dos.write(test);
        e.compare("ByteArray full");
    }
    
    @Test
    public void testByteArrayPart() throws Exception {
        byte [] test = getByteArray();
        Engine e = new Engine();
        e.bb.write(test, 3, 23);
        e.dos.write(test, 3, 23);
        e.compare("ByteArray part");
    }
    
    @Test
    public void test1() throws Exception {
        Engine e = new Engine();
        int data = 0;
        for (int i = 0; i < 23; ++i) {
            e.bb.write(data);
            e.dos.write(data);
            data = 5 * data + 1;
        }
        e.compare("1");
    }
    
    @Test
    public void testByte() throws Exception {
        Engine e = new Engine();
        int data = 0;
        for (int i = 0; i < 23; ++i) {
            e.bb.write(data);
            e.dos.write(data);
            data = 5 * data + 1;
        }
        e.compare("Byte");
    }


    @Test
    public void testShort() throws Exception {
        Engine e = new Engine();
        int data = 0;
        for (int i = 0; i < 23; ++i) {
            e.bb.writeShort(data);
            e.dos.writeShort(data);
            data = 5 * data + 1;
        }
        e.compare("Byte");
    }

    @Test
    public void testInt() throws Exception {
        Engine e = new Engine();
        int data = 0;
        for (int i = 0; i < 23; ++i) {
            e.bb.writeInt(data);
            e.dos.writeInt(data);
            data = 5 * data + 1;
        }
        e.compare("Int");
    }

    @Test
    public void testLong() throws Exception {
        Engine e = new Engine();
        long data = 0;
        for (int i = 0; i < 77; ++i) {
            e.bb.writeLong(data);
            e.dos.writeLong(data);
            data = 5 * data + 1;
        }
        e.compare("Long");
    }

    @Test
    public void testChar() throws Exception {
        Engine e = new Engine();
        int data = 0;
        for (int i = 0; i < 23; ++i) {
            e.bb.writeChar(data);
            e.dos.writeShort(data);
            data = 5 * data + 1;
        }
        e.compare("Char");
    }
}
