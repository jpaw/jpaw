package de.jpaw.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import de.jpaw.json.JsonException;
import de.jpaw.util.CharTestsASCII;

public class JsonParser {
    private final boolean useFloat;
    private final CharSequence s;
    private final int len;
    private int i;

    public JsonParser(CharSequence s, boolean useFloat) {
        this.s = s;
        this.useFloat = useFloat;
        len = s == null ? 0 : s.length();
        i = 0;
    }

    private void skipSpaces() {
        while (i < len && Character.isWhitespace(s.charAt(i)))
            ++i;
    }

    private char peekNeededChar() throws JsonException {
        if (i >= len)
            throw new JsonException(JsonException.JSON_PREMATURE_END, i);
        return s.charAt(i);
    }

    private void mustEnd() throws JsonException {
        skipSpaces();
        if (i < len)
            throw new JsonException(JsonException.JSON_GARBAGE_AT_END, i);

    }

    // return true if the next characters in the input sequence match txt
    protected boolean nextStartsWith(String txt) {
        final int len1 = txt.length();
        if (i + len1 > len)
            return false;           // too short
        for (int j = 0; j < len1; ++j)
            if (s.charAt(i + j) != txt.charAt(j))
                return false;
        return true;
    }

    // check if the next token is the one expected, then advance and return true, else false
    private boolean peek(String txt) throws JsonException {
        if (!nextStartsWith(txt))
            return false;
        i += txt.length();
        // check
        if (CharTestsASCII.isAsciiLowerCase(txt.charAt(0)) && i < len) {
            // cannot have a keyword character following
            char c = s.charAt(i);
            if (CharTestsASCII.isJavascriptIdChar(c))
                throw new JsonException(JsonException.JSON_BAD_IDENTIFIER, i);
        }
        skipSpaces();
        return true;
    }

    private void requireNext(char wanted) throws JsonException {
        skipSpaces();
        char c = peekNeededChar();
        if (c != wanted) {
            throw new JsonException(JsonException.JSON_SYNTAX, "Expected character '" + wanted + "' at pos " + i
                    + ", but found '" + c + "'");
        }
        ++i;
    }

    // parse a string which contains an ID
    private String parseId() throws JsonException {
//        requireNext('"');
//        StringBuilder sb = new StringBuilder(40);
//        char c = s.charAt(i);
////        // cannot start with a digit
////        if (CharTestsASCII.isAsciiDigit(c))
////            throw new JsonException(JsonException.JSON_BAD_IDENTIFIER, i);
//        while (CharTestsASCII.isJavascriptIdChar(c)) {
//            sb.append(c);
//            ++i;
//            c = peekNeededChar();
//        }
//        if (c != '\"' || sb.length() == 0)
//            throw new JsonException(JsonException.JSON_BAD_IDENTIFIER, i);
//        ++i;
//        skipSpaces();
//        return sb.toString();
        String s = parseStringSub();
        if (s.length() == 0 || CharTestsASCII.isAsciiDigit(s.charAt(0)))
            throw new JsonException(JsonException.JSON_BAD_IDENTIFIER, i);
        return s;
    }

// table based implementation: less jumps, but may need additional memory access and therefore be slower
//    private static final byte [] HEX_VALUES = {
//         0,  1,  2,  3,  4,  5,  6,  7,   8,  9, -1, -1, -1, -1, -1, -1,
//        -1, -1, -1, -1, -1, -1, -1, -1,  -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, 10, 11, 12, 13, 14, 15, -1,  -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, -1, -1, -1, -1, -1, -1, -1,  -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, 10, 11, 12, 13, 14, 15, -1,  -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, -1, -1, -1, -1, -1, -1, -1,  -1, -1, -1, -1, -1, -1, -1, -1
//    };
//
//    // return the value of the next hex digit
//    private int nextHex() throws JsonException {
//        char c = peekNeededChar();
//        ++i;
//        if (c < '0' || c > 'f')
//            throw new JsonException(JsonException.JSON_BAD_ESCAPE, i);
//        int digit = HEX_VALUES[c - '0'];
//        if (digit < 0)
//            throw new JsonException(JsonException.JSON_BAD_ESCAPE, i);
//        return digit;
//    }

    // return the value of the next hex digit
    private int nextHex() throws JsonException {
        char c = peekNeededChar();
        ++i;
        if (c >= '0' && c <= 'f') {
            if (c <= '9')
                return c - '0';
            if (c >= 'a')
                return c - 'a' + 10;
            if (c >= 'A' && c <= 'F')
                return c - 'A' + 10;
        }
        throw new JsonException(JsonException.JSON_BAD_ESCAPE, i);
    }

    // parse a string which contains a generic string
    private String parseStringSub() throws JsonException {
        requireNext('"');
        StringBuilder sb = new StringBuilder(40);
        char c = peekNeededChar();
        ++i;
        while (c != '\"') {
            if (c == '\\') {
                // unescape!
                c = peekNeededChar();
                ++i;
                switch (c) {
                case 'b':
                    c = '\b';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 'f':
                    c = '\f';
                    break;
                case 'n':
                    c = '\n';
                    break;
                case 't':
                    c = '\t';
                    break;
                case 'u':       // Unicode escape
                    // need 4 more characters
                    if (i + 4 > len)
                        throw new JsonException(JsonException.JSON_BAD_ESCAPE, i);
                    int cc = nextHex() << 24;
                    cc |= nextHex() << 16;
                    cc |= nextHex() << 8;
                    cc |= nextHex();
                    c = (char)cc;
                    break;
                default:        // use c 1:1
                }
            }
            sb.append(c);
            c = peekNeededChar();
            ++i;
        }
        skipSpaces();
        return sb.toString();
    }

    // s is not null
    // parse an object starting a the current pos
    private Object parseElementSub() throws JsonException {
        skipSpaces();
        char c = peekNeededChar();
        switch (c) {
        case 'n':
            if (peek("null")) {
                return null;
            }
            break;
        case 't':
            if (peek("true")) {
                return Boolean.TRUE;
            }
            break;
        case 'f':
            if (peek("false")) {
                return Boolean.FALSE;
            }
            break;
        case '{':
            return parseMapSub();
        case '[':
            return parseListSub();
        case '\"':
            return parseStringSub();
        }
        StringBuilder sb = new StringBuilder(50);
        if (CharTestsASCII.isJavascriptNumberChar(c)) {
            // in Java, distinguish between integral and float numbers. Also, use BigDecimal or Double, according to preference.
            // check for integral numbers
            if (c == '+' || c == '-') {
                sb.append(c);
                ++i;
                c = peekNeededChar();
            }
            try {
                if (CharTestsASCII.isAsciiDigit(c)) {
                    // attempt an integral number
                    do {
                        sb.append(c);
                        ++i;
                        if (i == len) {
                            c = 0;
                            break;
                        }
                        c = s.charAt(i);
                    } while (CharTestsASCII.isAsciiDigit(c));
                    if (!CharTestsASCII.isJavascriptNumberChar(c)) {
                        // pattern is integral
                        long l = Long.parseLong(sb.toString());
                        skipSpaces();
                        if((int)l == l)
                            return Integer.valueOf((int)l);
                        return Long.valueOf(l);
                    }
                    // fall through to fractional numbers
                }
                do {
                    sb.append(c);
                    ++i;
                    if (i == len) {
                        c = 0;
                        break;
                    }
                    c = s.charAt(i);
                } while (CharTestsASCII.isJavascriptNumberChar(c));
                skipSpaces();
                return useFloat ? Double.parseDouble(sb.toString()) : new BigDecimal(sb.toString());
            } catch (NumberFormatException e) {
                throw new JsonException(JsonException.JSON_BAD_NUMBER, i);
            }
        }
        throw new JsonException(JsonException.JSON_SYNTAX, i);
    }

    private List<Object> parseListSub()  throws JsonException {
        final List<Object> list = new ArrayList<Object>();
        ++i;
        skipSpaces();

        boolean needComma = false;
        // add elements until "]" is found
        char c = peekNeededChar();
        while (c != ']') {
            if (needComma)
                requireNext(',');
            list.add(parseElementSub());
            c = peekNeededChar();
            needComma = true;
        }
        ++i;
        skipSpaces();
        return list;
    }

    // the current char definitely is '{'. Parse a non-null Map.
    private Map<String, Object> parseMapSub() throws JsonException {
        final Map<String, Object> map = new HashMap<String, Object>();
        ++i;
        skipSpaces();

        boolean needComma = false;
        // loop through key / value pairs
        char c = peekNeededChar();
        while (c != '}') {
            if (needComma)
                requireNext(',');
            // parse one key / value pair
            String key = parseId();
            requireNext(':');
            map.put(key, parseElementSub());
            skipSpaces();
            c = peekNeededChar();
            needComma = true;
        }
        ++i;
        skipSpaces();
        return map;
    }

    public Object parseElement() throws JsonException {
        if (s == null)
            return null;    // shortcut
        Object obj = parseElementSub();
        mustEnd();
        return obj;
    }

    /** Parses a single object and returns it as a map (subroutine). */
    public Map<String, Object> parseObjectSub() throws JsonException {
        skipSpaces();
        char c = peekNeededChar();
        if (c == 'n' && nextStartsWith("null")) {
            i += 4;
            skipSpaces();
            return null;
        }
        if (c != '{')
            throw new JsonException(JsonException.JSON_SYNTAX, i);
        Map<String, Object> map = parseMapSub();
        return map;
    }

    /** Parses a single object and returns it as a map. */
    public Map<String, Object> parseObject() throws JsonException {
        if (s == null)
            return null;    // shortcut
        Map<String, Object> map = parseObjectSub();
        mustEnd();
        return map;
    }

    /** Parses a list of elements and returns it as a list. */
    public List<Object> parseArray() throws JsonException {
        if (s == null)
            return null;    // shortcut
        skipSpaces();
        char c = peekNeededChar();
        if (c == 'n' && nextStartsWith("null")) {
            i += 4;
            mustEnd();
            return null;
        }
        if (c != '[')
            throw new JsonException(JsonException.JSON_SYNTAX, i);
        List<Object> l = parseListSub();
        mustEnd();
        return l;
    }

    /** Expect either a list of objects, or a single object, or null. Emits all parsed objects via the provided consumer. */
    public void parseObjectOrListOfObjects(Consumer<Map<String, Object>> sink) throws JsonException {
        if (s == null)
            return;    // shortcut (nothing is emitted)
        skipSpaces();
        if (i >= len) {
            // empty string: do not emit anything
            return;
        }
        char c = peekNeededChar();
        if (c != '[') {
            // must be a single object or null
            Map<String, Object> map = parseObjectSub();
            sink.accept(map);  // emit single object
            mustEnd();
            return;
        }
        ++i;
        skipSpaces();
        c = peekNeededChar();
        if (c == ']') {
            // empty list
            ++i;
            mustEnd();
            return;
        }
        // parse a list of objects
        for (;;) {
            Map<String, Object> map = parseObjectSub();
            sink.accept(map);  // emit parsed object (list element)
            // now expect a comma or end of list
            c = peekNeededChar();
            if (c == ']') {
                ++i;
                mustEnd();
                return;
            }
            if (c != ',') {
                throw new JsonException(JsonException.JSON_SYNTAX, "Expected character ',' or ']' at pos " + i);
            }
            ++i;  // skip ','
        }
    }
}
