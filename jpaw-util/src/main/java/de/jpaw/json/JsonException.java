package de.jpaw.json;

import de.jpaw.util.ApplicationException;

/**
 * The JsonException class.
 *
 * @author Michael Bischoff
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to JSON parsing.
 */

public class JsonException extends ApplicationException {
    private static final long serialVersionUID = 853020616281351L;

    private static final int OFFSET = CL_VALIDATION_ERROR * CLASSIFICATION_FACTOR + 14000;  // offset for all codes in this class

    static public final int JSON_GARBAGE_AT_END          = OFFSET + 1;
    static public final int JSON_BAD_IDENTIFIER          = OFFSET + 2;
    static public final int JSON_BAD_STRING              = OFFSET + 3;
    static public final int JSON_BAD_NUMBER              = OFFSET + 4;
    static public final int JSON_SYNTAX                  = OFFSET + 5;
    static public final int JSON_PREMATURE_END           = OFFSET + 6;
    static public final int JSON_BAD_ESCAPE              = OFFSET + 7;

    static {
        codeToDescription.put(JSON_PREMATURE_END            , "Premature end of embedded JSON object");
        codeToDescription.put(JSON_GARBAGE_AT_END           , "Garbage at end of embedded JSON object");
        codeToDescription.put(JSON_BAD_IDENTIFIER           , "Bad JSON identifier");
        codeToDescription.put(JSON_BAD_STRING               , "Bad string in JSON");
        codeToDescription.put(JSON_BAD_NUMBER               , "Malformed number in JSON");
        codeToDescription.put(JSON_SYNTAX                   , "Invalid JSON syntax");
        codeToDescription.put(JSON_BAD_ESCAPE               , "Invalid JSON (unicode) escape sequence");
    }

    public JsonException(int errorCode, int pos) {
        super(errorCode, String.format("At pos %d", pos));
    }

    public JsonException(int errorCode, String text) {
        super(errorCode, text);
    }

    public JsonException(int errorCode) {
        this(errorCode, null);
    }

}
