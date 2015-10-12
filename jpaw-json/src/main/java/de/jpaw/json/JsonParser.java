package de.jpaw.json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
                throw new JsonException(JsonException.JSON_SYNTAX, i);
        }
        skipSpaces();
        return true;
    }
    
    private void requireNext(char wanted) throws JsonException {
        skipSpaces();
        char c = peekNeededChar();
        if (c != wanted)
            throw new JsonException(JsonException.JSON_SYNTAX, i);
        ++i;
    }
    
    // parse a string which contains an ID
    private String parseId() throws JsonException {
        requireNext('"');
        StringBuilder sb = new StringBuilder(40);
        char c = s.charAt(i);
        while (CharTestsASCII.isJavascriptIdChar(c)) {
            sb.append(c);
            ++i;
            c = peekNeededChar();
        }
        if (c != '\"' || sb.length() == 0)
            throw new JsonException(JsonException.JSON_SYNTAX, i);
        ++i;
        skipSpaces();
        return sb.toString();
    }
    
    // parse a string which contains a generic string
    private String parseStringSub() throws JsonException {
        requireNext('"');
        StringBuilder sb = new StringBuilder(40);
        char c = s.charAt(i);
        while (c != '\"') {
            sb.append(c);           // TODO: unescape!
            ++i;
            c = peekNeededChar();
        }
        ++i;
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
                return useFloat ? new Double(sb.toString()) : new BigDecimal(sb.toString());
            } catch (NumberFormatException e) {
                throw new JsonException(JsonException.JSON_BAD_NUMBER, i);
            }
        }
        throw new JsonException(JsonException.JSON_SYNTAX, i);
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
    
    public Map<String, Object> parseObject() throws JsonException {
        if (s == null)
            return null;    // shortcut
        skipSpaces();
        char c = peekNeededChar();
        if (c == 'n' && nextStartsWith("null")) {
            mustEnd();
            return null;
        }
        if (c != '{')
            throw new JsonException(JsonException.JSON_SYNTAX, i);
        Map<String, Object> map = parseMapSub();
        mustEnd();
        return map;
    }
}
