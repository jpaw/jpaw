 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package de.jpaw.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.nio.charset.Charset;


/**
 *          Functionality which corresponds to String, but for byte arrays.
 *          Essential feature is that the class is immutable, so you can use it in messaging without making deep copies.
 *          Mimicking {@link java.lang.String}, the class contains offset and length fields to allow sharing of the buffer.
 *          <p>
 *          This should really exist in Java SE already.
 *
 * @author Michael Bischoff
 *
 */


public final class ByteArray implements Externalizable, Cloneable {
    private static final long serialVersionUID = 2782729564297256974L;
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");    // default character set is available on all platforms
    private static final int MAGIC_LENGTH_INDICATING_32_BIT_SIZE = 247;  // if a single byte length of this value is written in the
    // serialized form, it indicates a full four byte length must be read instead. Not used 0 or 255 due to their frequent use.

    private final byte[] buffer;
    private final int offset;
    private final int length;
    private ByteArray extraFieldJustRequiredForDeserialization = null;  // transient temporary field

    private static final byte[] ZERO_JAVA_BYTE_ARRAY = new byte[0];
    public static final ByteArray ZERO_BYTE_ARRAY = new ByteArray(ZERO_JAVA_BYTE_ARRAY);

    /** No-arg constructor required for Serializable interface. */
    @Deprecated
    public ByteArray() {
        this(ZERO_JAVA_BYTE_ARRAY);
    }

    /** Constructs a ByteArray from a source byte[], which is defensively copied. */
    public ByteArray(final byte[] source) {
        if (source == null || source.length == 0) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = source.clone();  // benchmarks have shown that clone() is equally fast as System.arraycopy for all lengths > 0
            offset = 0;
            length = buffer.length;
        }
    }

    // construct a ByteArray from a trusted source byte[]
    // this method is always called with unsafeTrustedReuseOfJavaByteArray = true, the parameter is only required in order to distinguish the constructor
    // from the copying one
    private ByteArray(final byte[] source, final boolean unsafeTrustedReuseOfJavaByteArray) {
        if (source == null || source.length == 0) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = unsafeTrustedReuseOfJavaByteArray ? source : source.clone();
            offset = 0;
            length = buffer.length;
        }
    }

    /** Constructs a ByteArray from a ByteArrayOutputStream, which has just been contructed by some previous process.
     * @throws IOException */
    public static ByteArray fromByteArrayOutputStream(final ByteArrayOutputStream baos) throws IOException {
        baos.flush();
        return new ByteArray(baos.toByteArray(), true);
    }

    /** Writes the contents of this ByteArray to an OutputStream. */
    public void toOutputStream(final OutputStream os) throws IOException {
        os.write(buffer, offset, length);
    }

    /** Constructs a ByteArray from the provided DataInput, with a predefined length. */
    public static ByteArray fromDataInput(final DataInput in, final int len) throws IOException {
        if (len <= 0)
            return ZERO_BYTE_ARRAY;
        final byte[] tmp = new byte[len];
        in.readFully(tmp);
        return new ByteArray(tmp, true);
    }

    /** read bytes from an input stream, up to maxBytes (or all which exist, if maxBytes = 0). */
    public static ByteArray fromInputStream(final InputStream is, final int maxBytes) throws IOException {
        final ByteBuilder tmp = maxBytes > 0 ? new ByteBuilder(maxBytes, CHARSET_UTF8) : new ByteBuilder();
        tmp.readFromInputStream(is, maxBytes);
        if (tmp.length() == 0)
            return ZERO_BYTE_ARRAY;
        return new ByteArray(tmp.getCurrentBuffer(), 0, tmp.length());
    }

    /** Constructs a ByteArray from the provided ByteBuilder. */
    public static ByteArray fromByteBuilder(final ByteBuilder in) {
        if (in == null || in.length() == 0)
            return ZERO_BYTE_ARRAY;
        return new ByteArray(in.getCurrentBuffer(), 0, in.length());
    }

    /** Constructs a ByteArray from the provided String, using the UTF8 character set. */
    public static ByteArray fromString(final String in) {
        return fromString(in, CHARSET_UTF8);
    }

    /** Constructs a ByteArray from the provided String, using the specified character set. */
    public static ByteArray fromString(final String in, final Charset cs) {
        if (in == null || in.length() == 0)
            return ZERO_BYTE_ARRAY;
        return new ByteArray(in.getBytes(cs), true);    // we know these bytes are never changed, so no extra copy required
    }

    /** returns the byte array as a string. Unlike toString(), which uses the JVM default character set, this method always uses UTF-8. */
    public String asString() {
        return asString(CHARSET_UTF8);
    }

    /** returns the byte array as a string, using a specified character set. */
    public String asString(final Charset cs) {
        return new String(buffer, offset, length, cs);
    }

    /** construct a ByteArray from a source byte[], with offset and length. source may not be null. */
    public ByteArray(final byte[] source, final int offset, final int length) {
        if (source == null || offset < 0 || length < 0 || offset + length > source.length)
            throw new IllegalArgumentException();
        buffer = new byte[length];
        System.arraycopy(source, offset, buffer, 0, length);
        this.offset = 0;
        this.length = length;
    }

    /** Construct a ByteArray from another one. Could also just assign it due to immutability.
     * The only benefit of this constructor is that it converts a null parameter into the non-null empty ByteArray. */
    public ByteArray(final ByteArray source) {
        if (source == null) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = source.buffer;  // no array copy required due to immutability
            offset = source.offset;
            length = source.length;
        }
    }

    /** Construct a ByteArray from a source byte[], with offset and length. source may not be null.
     * Similar to the subArray member method. */
    public ByteArray(final ByteArray source, final int offset, final int length) {
        if (source == null || offset < 0 || length < 0 || offset + length > source.length)
            throw new IllegalArgumentException();

        this.buffer = source.buffer;  // no array copy required due to immutability
        this.offset = source.offset + offset;
        this.length = length;
    }

    /** Returns a ByteArray which contains a subsequence of the bytes of this one. The underlying buffer is shared.
     * Functionality wise this corresponds to String.substring (before Java 6) or ByteBuffer.slice. */
    public ByteArray subArray(final int xoffset, final int xlength) {
        // create a new ByteArray sharing the same buffer
        return new ByteArray(this, xoffset, xlength);
    }

    /** Returns a ByteArray which contains a subsequence of the bytes of this one. The underlying buffer is not shared.
     * Use this variant if the original ByteArray holds a much larger byte[] and can be GCed afterwards. */
    public ByteArray subArrayUnshared(final int xoffset, final int xlength) {
        if (xoffset < 0 || xlength < 0 || xoffset + xlength > this.length)
            throw new IllegalArgumentException();
        final byte[] newBuffer = new byte[xlength];
        System.arraycopy(buffer, xoffset, newBuffer, 0, xlength);
        // create a new ByteArray using the new buffer
        return new ByteArray(newBuffer, true);
    }

    @Override
    public ByteArray clone() {
        return new ByteArray(this);
    }

    public int length() {
        return this.length;
    }

//    public int getOffset() {
//        return this.offset;
//    }
//
//    /** Returns the internal buffer of this object. It may only be used for read-only access.
//     * Java is missing a "const" specifier for arrays as it is available in C and C++.
//     *
//     * Java-purists will complain against exposing this internal state of an immutable object, but as long as
//     * access is possible via reflection anyway, just with performance penalty, it would be outright stupid
//     * to force people to use reflection, or even defensive copies. Instead I hope the name of the method
//     * documents the intended use.
//     */
//    public byte /* const */[] unsafe$getConstBufferOfConstBytes() {
//        return this.buffer;
//    }

    public int indexOf(final byte x) {
        int i = 0;
        while (i < length) {
            if (buffer[offset + i] == x)
                return i;
            ++i;
        }
        return -1;
    }

    public int indexOf(final byte x, final int fromIndex) {
        int i = fromIndex >= 0 ? fromIndex : 0;
        while (i < length) {
            if (buffer[offset + i] == x)
                return i;
            ++i;
        }
        return -1;
    }

    public int lastIndexOf(final byte x) {
        int i = length;
        while (i > 0) {
            if (buffer[offset + --i] == x)
                return i;
        }
        return -1;
    }

    public int lastIndexOf(final byte x, final int fromIndex) {
        int i = fromIndex >= length ? length - 1 : fromIndex;
        while (i >= 0) {
            if (buffer[offset + i] == x)
                return i;
            --i;
        }
        return -1;
    }

    public byte byteAt(final int pos) {
        if (pos < 0 || pos >= length)
            throw new IllegalArgumentException();
        return buffer[offset + pos];
    }

    /** Provides the contents of this ByteArray to some InputStream. */
    public ByteArrayInputStream asByteArrayInputStream() {
        return new ByteArrayInputStream(buffer, offset, length());
    }

    // return a defensive copy of the contents
    public byte[] getBytes() {
        final byte[] result = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);
        return result;
    }

    // return a defensive copy of part of the contents. Shorthand for subArray(offset, length).getBytes(),
    // which would create a temporary object
    public byte[] getBytes(final int xoffset, final int xlength) {
        if (xoffset < 0 || xlength < 0 || xoffset + xlength > this.length)
            throw new IllegalArgumentException();
        final byte[] result = new byte[xlength];
        System.arraycopy(buffer, xoffset + this.offset, result, 0, xlength);
        return result;
    }

    private boolean contentEqualsSub(final byte[] dst, final int dstOffset, final int dstLength) {
        if (length != dstLength)
            return false;
        for (int i = 0; i < dstLength; ++i) {
            if (buffer[offset + i] != dst[dstOffset + i])
                return false;
        }
        return true;
    }

    // following: all arguments must be not null
    public boolean contentEquals(final ByteArray that) {
        return contentEqualsSub(that.buffer, that.offset, that.length);
    }
    public boolean contentEquals(final byte[] that) {
        return contentEqualsSub(that, 0, that.length);
    }
    public boolean contentEquals(final byte[] that, final int thatOffset, final int thatLength) {
        if (thatOffset < 0 || thatLength < 0 || thatOffset + thatLength > that.length)
            throw new IllegalArgumentException();
        return contentEqualsSub(that, thatOffset, thatLength);
    }

    // returns if the two instances share the same backing buffer (for debugging)
    public boolean shareBuffer(final ByteArray that) {
        return buffer == that.buffer;
    }

    @Override
    public int hashCode() {
        int hash = 997;
        for (int i = 0; i < length; ++i) {
            hash = 29 * hash + buffer[offset + i];
        }
        return hash;
    }

    // two ByteArrays are considered equal if they have the same visible contents
    @Override
    public boolean equals(final Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;
        final ByteArray xthat = (ByteArray)that;
        // same as contentEqualsSub(..) now
        if (this.length != xthat.length)
            return false;
        for (int i = 0; i < length; ++i) {
            if (buffer[offset + i] != xthat.buffer[xthat.offset + i])
                return false;
        }
        return true;
    }

    // support function to allow dumping contents to DataOutput without the need to expose our internal buffer
    public void writeToDataOutput(final DataOutput out) throws IOException {
        out.write(buffer, offset, length);
    }

    public String hexdump(final int startAt, final int maxlength) {
        if (length <= startAt)
            return "";      // no data to dump
        return ByteUtil.dump(buffer, offset + startAt, (maxlength > 0 && maxlength < length) ? maxlength : length);
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        //writeBytes(out, buffer, offset, length);
        if (length < 256 && length != MAGIC_LENGTH_INDICATING_32_BIT_SIZE) {
            out.writeByte(length);
        } else {
            out.writeByte(MAGIC_LENGTH_INDICATING_32_BIT_SIZE);
            out.writeInt(length);
        }
        out.write(buffer, offset, length);
    }

    // support function to allow ordinary byte[] to be written in same fashion
    public static void writeBytes(final ObjectOutput out, final byte[] buffer, final int offset, final int length) throws IOException {
        if (length < 256 && length != MAGIC_LENGTH_INDICATING_32_BIT_SIZE) {
            out.writeByte(length);
        } else {
            out.writeByte(MAGIC_LENGTH_INDICATING_32_BIT_SIZE);
            out.writeInt(length);
        }
        out.write(buffer, offset, length);
    }

    public static byte[] readBytes(final ObjectInput in) throws IOException {
        int newlength = in.readByte();
        if (newlength < 0)
            newlength += 256;  // want full unsigned range
        if (newlength == MAGIC_LENGTH_INDICATING_32_BIT_SIZE) // magic to indicate four byte length
            newlength = in.readInt();

        // System.out.println("ByteArray.readExternal() with length " + newlength);
        if (newlength == 0)
            return ZERO_JAVA_BYTE_ARRAY;
        final byte[] localBuffer = new byte[newlength];
        int done = 0;
        while (done < newlength) {
            final int nRead = in.read(localBuffer, done, newlength - done);  // may return less bytes than requested!
            if (nRead <= 0)
                throw new IOException("deserialization of ByteArray returned " + nRead + " while expecting " + (newlength - done));
            done += nRead;
        }
        return localBuffer;
    }

    // factory method to read from objectInput via above helper function
    public static ByteArray read(final ObjectInput in) throws IOException {
        return new ByteArray(readBytes(in), true);
    }

    // a direct implementation of this method would conflict with the immutability / "final" attributes of the field
    // Weird Java language design again. If readExternal() is kind of a constructor, why are assignments to final fields not allowed here?
    // alternatives around are to add artificial fields and use readResolve / proxies or to discard the "final" attributes,
    // or using reflection to set the values (!?). Bleh!
    // We're using kind of Bloch's "proxy" pattern (Essential Java, #78), namely a single-sided variant with just a single additonal member field,
    // which lets us preserve the immutability
    // see also http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6379948 for discussion around this
    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        extraFieldJustRequiredForDeserialization = new ByteArray(readBytes(in), true);
    }

    public Object readResolve() {
        // System.out.println("ByteArray.readResolve()");
        if (extraFieldJustRequiredForDeserialization == null)
            throw new RuntimeException("readResolve() called on instance not obtained via readExternal()");
        return extraFieldJustRequiredForDeserialization;
    }

    // factory method to construct a byte array from a prevalidated base64 byte sequence. returns null if length is suspicious
    public static ByteArray fromBase64(final byte[] data, final int offset, final int length) {
        if (length == 0)
            return ZERO_BYTE_ARRAY;
        final byte[] tmp = Base64.decode(data, offset, length);
        if (tmp == null)
            return null;
        return new ByteArray(tmp, true);
    }

    public void appendBase64(final ByteBuilder b) {
        Base64.encodeToByte(b, buffer, offset, length);
    }
    public void appendToRaw(final ByteBuilder b) {
        b.write(buffer, offset, length);
    }

    /** Returns the contents of this ByteArray as a base64 encoded string.
     * @since 1.2.12 */
    public String asBase64() {
        final ByteBuilder tmp = new ByteBuilder(0, null);
        Base64.encodeToByte(tmp, buffer, offset, length);
        return tmp.toString();
    }

    // returns the String representation of the visible bytes portion
    @Override
    public String toString() {
        return new String(buffer, offset, length);
    }
}
