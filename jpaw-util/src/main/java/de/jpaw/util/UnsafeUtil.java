package de.jpaw.util;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtil {
    private static final Unsafe unsafe;
    private static final long stringValueOffset;      // Java String char[]
    private static final long byteBufferAddressOffset;// Byte Buffer address

    static {
        Unsafe finalUnsafe = null;
        try {
            // get the Unsafe class instance
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            finalUnsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
        }
        unsafe = finalUnsafe;

        long finalStringValueOffset = 0L;
        if (unsafe != null) {
            try {
                // get the offset of the char buffer
                finalStringValueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
                // test for older JDK / Runtime lib, which had extra offset
                try {
                    String.class.getDeclaredField("offset");  // check for exception here
                    finalStringValueOffset = 0L;
                } catch (NoSuchFieldException e) {
                    // OK, finalStringValueOffset already set
                }
            } catch (Exception e) {
                // not OK, finalStringValueOffset still 0
            }
        }
        stringValueOffset = finalStringValueOffset;
        
        long finalAddressOffset = 0L;
        if (unsafe != null) {
            try {
                // get the offset of the nio buffer
                finalAddressOffset = unsafe.objectFieldOffset(Buffer.class.getDeclaredField("address"));
            } catch (Exception e) {
                // not OK, finalAddressOffset still 0
            }
        }
        byteBufferAddressOffset = finalAddressOffset;

//        System.out.println("UnsafeUtil initialized - String buffer " + (stringValueOffset == 0L ? " does NOT work" : "works"));
    }
    
    public static char [] getStringBuffer(String s) {
        if (stringValueOffset != 0L) {
            // steal buffer
            return (char[]) unsafe.getObject(s, stringValueOffset);
        } else {
            return s.toCharArray();     // COPY as a fallback for JDP 6 
        }
    }
    
    public static long getAddress(ByteBuffer buff) {
        return unsafe.getLong(buff, byteBufferAddressOffset);
    }
    
    public static void putByte(long address, byte b) {
        unsafe.putByte(address, b);
    }
}
