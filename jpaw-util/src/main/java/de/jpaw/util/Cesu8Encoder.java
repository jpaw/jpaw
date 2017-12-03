package de.jpaw.util;

import java.util.Arrays;

public class Cesu8Encoder {
    public static byte [] encodeToCesu8(final String s) {
        int bytesNeeded = 0;
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            bytesNeeded += c < 2048 ? (c < 128 ? 1 : 2) : 3;
        }
        final byte [] buff = new byte [bytesNeeded];
        int j = -1;
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if (c < 2048) {
                if (c < 128) {
                    buff[++j] = (byte)c;
                } else {
                    buff[++j] = (byte)(0xc0 + (c >> 6));
                    buff[++j] = (byte)(0x80 + (c & 0x3f));
                }
            } else {
                buff[++j] = (byte) (0xe0 + (c >> 12));
                buff[++j] = (byte) (0x80 + ((c >> 6) & 0x3f));
                buff[++j] = (byte) (0x80 + (c & 0x3f));
            }
        }
        return buff;
    }
    /** Same method, but does a single pass and does an array copy in the end. */
    public static byte [] encodeToCesu8Copy(final String s) {
        final int len = s.length();
        final byte [] buff = new byte [len << 2];
        int j = -1;
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if (c < 2048) {
                if (c < 128) {
                    buff[++j] = (byte)c;
                } else {
                    buff[++j] = (byte)(0xc0 + (c >> 6));
                    buff[++j] = (byte)(0x80 + (c & 0x3f));
                }
            } else {
                buff[++j] = (byte) (0xe0 + (c >> 12));
                buff[++j] = (byte) (0x80 + ((c >> 6) & 0x3f));
                buff[++j] = (byte) (0x80 + (c & 0x3f));
            }
        }
        return Arrays.copyOf(buff, j+1);    // create an array of appropriate length
    }
}
