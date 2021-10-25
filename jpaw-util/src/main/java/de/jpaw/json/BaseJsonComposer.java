package de.jpaw.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public class BaseJsonComposer implements JsonEscaper {
    private static final int ESCAPE_TAB_SIZE = 128;      // the number of sequences defined in the tab
    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static String[] jsonEscapes = new String[ESCAPE_TAB_SIZE];
    // initialize the escape sequences
    static {
        // preset special escapes
        jsonEscapes['\b'] = "\\b";
        jsonEscapes['\f'] = "\\f";
        jsonEscapes['\r'] = "\\r";
        jsonEscapes['\n'] = "\\n";
        jsonEscapes['\t'] = "\\t";
        jsonEscapes['\"'] = "\\\"";
        jsonEscapes['\\'] = "\\\\";
        // preset other control characters
        for (int i = 0; i < 32; ++i) {
            if (jsonEscapes[i] == null)
                jsonEscapes[i] = "\\u00" + HEX_CHARS[i / 16] + HEX_CHARS[i & 15];
        }
    }

    protected final Appendable appendable;
    protected final boolean writeNulls;
    protected final boolean escapeNonASCII;

    public BaseJsonComposer(final Appendable appendable) {
        this.appendable     = appendable;
        this.writeNulls     = true;
        this.escapeNonASCII = false;
    }

    public BaseJsonComposer(final Appendable appendable, final boolean writeNulls, final boolean escapeNonASCII) {
        this.appendable     = appendable;
        this.writeNulls     = writeNulls;
        this.escapeNonASCII = escapeNonASCII;       // escape all non-ASCII-chars (required for sockJS)
    }

    @Override
    public void writeUnicodeEscape(final char c) throws IOException {
        appendable.append('\\');
        appendable.append('u');
        appendable.append(HEX_CHARS[(c >> 12) & 0xF]);
        appendable.append(HEX_CHARS[(c >> 8) & 0xF]);
        appendable.append(HEX_CHARS[(c >> 4) & 0xF]);
        appendable.append(HEX_CHARS[c & 0xF]);
    }

    /** Writes a quoted string. We know that we don't need escaping. */
    @Override
    public void outputAscii(final String s) throws IOException {
        appendable.append('"');
        appendable.append(s);
        appendable.append('"');
    }

    @Override
    public void outputUnicodeNoControls(final String s) throws IOException {
        if (escapeNonASCII) {
            outputUnicodeWithControls(s);       // nonstd - need to check as well in this case
        } else {
            outputAscii(s);                     // same as ASCII
        }
    }

    /** Write the String s (which may not be null) to the Appendable.
     * This implementation may not yet be fully Unicode-compliant.
     * See here for the explanation: http://stackoverflow.com/questions/1527856/how-can-i-iterate-through-the-unicode-codepoints-of-a-java-string
     *  */
    @Override
    public void outputUnicodeWithControls(final String s) throws IOException {
        appendable.append('\"');
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if ((c & ~0x7f) != 0) {   // TODO: check if this works correctly for Unicodes characters of the upper plane
                // non-ASCII (0x80 and above)
                if (escapeNonASCII)
                    writeUnicodeEscape(c);
                else
                    appendable.append(c);
            } else {
                // ASCII char (0x00 - 0x7f)
                if (jsonEscapes[c] == null)
                    appendable.append(c);
                else
                    appendable.append(jsonEscapes[c]);
            }
        }
        appendable.append('\"');
    }

    @Override
    public void outputJsonObject(final Map<String, Object> obj) throws IOException {
        if (obj == null) {
            appendable.append("null");
            return;
        }
        appendable.append('{');
        boolean needDelim = false;
        for (final Map.Entry<String, Object> elem: obj.entrySet()) {
            if (elem.getValue() != null || writeNulls) {
                if (needDelim)
                    appendable.append(',');
                outputUnicodeNoControls(elem.getKey());
                appendable.append(':');
                outputOptionalJsonElement(elem.getValue());
                needDelim = true;
            }
        }
        appendable.append('}');
    }

    @Override
    public void outputJsonArray(final List<?> obj) throws IOException {
        if (obj == null) {
            appendable.append("null");
            return;
        }
        boolean needDelim = false;
        appendable.append('[');
        for (final Object o : obj) {
            if (needDelim)
                appendable.append(',');
            outputOptionalJsonElement(o);
            needDelim = true;
        }
        appendable.append(']');
    }

    @Override
    public void outputJsonElement(final Object obj) throws IOException {
        if (obj instanceof Number) {
            outputNumber((Number)obj);
            return;
        }
        if (obj instanceof Boolean) {
            outputBoolean((Boolean)obj);
            return;
        }
        if (obj instanceof List<?>) {
            outputJsonArray((List<?>)obj);
            return;
        }
//        if (obj instanceof UUID) {
//            outputAscii(((UUID)obj).toString());
//            return;
//        }
        if (obj instanceof Set<?>) {
            boolean needDelim = false;
            appendable.append('[');
            for (final Object o : (Set<?>)obj) {
                if (needDelim)
                    appendable.append(',');
                outputOptionalJsonElement(o);
                needDelim = true;
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof Map<?, ?>) {
            outputJsonObject((Map<String, Object>)obj);
            return;
        }
        if (obj instanceof ByteArray) {
            outputAscii(((ByteArray)obj).toString());
            return;
        }
        // array type stuff. See http://stackoverflow.com/questions/219881/java-array-reflection-isarray-vs-instanceof
        // first, check of arrays of objects
        if (obj instanceof Object[]) {
            boolean needDelim = false;
            appendable.append('[');
            for (final Object o : (Object[])obj) {
                if (needDelim)
                    appendable.append(',');
                outputOptionalJsonElement(o);
                needDelim = true;
            }
            appendable.append(']');
            return;
        }
        if (obj.getClass().isArray()) {
            outputPrimitiveArrays(obj);
            return;
        }
        // last resort: use toString()
        outputUnicodeWithControls(obj.toString());      // UUID, Character
    }

    @Override
    public void outputNumber(final Number n) throws IOException {
        appendable.append(n.toString());
    }

    @Override
    public void outputBoolean(final boolean b) throws IOException {
        appendable.append(b ? "true" : "false");
    }

    // code moved out due to excessive length
    private void outputPrimitiveArrays(final Object obj) throws IOException {
        if (obj instanceof byte[]) {
            // special case: not an array, but a base64 encoded string
            final byte[] array = (byte[])obj;
            final ByteBuilder tmp = new ByteBuilder(0, null);
            Base64.encodeToByte(tmp, array, 0, array.length);
            outputAscii(tmp.toString());
            return;
        }
        if (obj instanceof int[]) {
            final int[] array = (int[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(Integer.toString(array[i]));
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof boolean[]) {
            final boolean[] array = (boolean[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(array[i] ? "true" : "false");
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof char[]) {
            final char[] array = (char[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                outputUnicodeWithControls(Character.toString(array[i]));    // converts char[] to a string
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof long[]) {
            final long[] array = (long[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(Long.toString(array[i]));
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof short[]) {
            final short[] array = (short[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(Short.toString(array[i]));
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof double[]) {
            final double[] array = (double[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(Double.toString(array[i]));
            }
            appendable.append(']');
            return;
        }
        if (obj instanceof float[]) {
            final float[] array = (float[])obj;
            appendable.append('[');
            for (int i = 0; i < array.length; ++i) {
                if (i > 0)
                    appendable.append(',');
                appendable.append(Float.toString(array[i]));
            }
            appendable.append(']');
            return;
        }
        throw new RuntimeException("Not yet supported: primitive array " + obj.getClass().getSimpleName());
    }

    @Override
    public void outputOptionalJsonElement(final Object obj) throws IOException {
        if (obj == null) {
            appendable.append("null");
        } else {
            outputJsonElement(obj);
        }
    }
}
