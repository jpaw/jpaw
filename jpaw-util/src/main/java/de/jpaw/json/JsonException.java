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

    public static final int JSON_GARBAGE_AT_END          = OFFSET + 1;
    public static final int JSON_BAD_IDENTIFIER          = OFFSET + 2;
    public static final int JSON_BAD_STRING              = OFFSET + 3;
    public static final int JSON_BAD_NUMBER              = OFFSET + 4;
    public static final int JSON_SYNTAX                  = OFFSET + 5;
    public static final int JSON_PREMATURE_END           = OFFSET + 6;
    public static final int JSON_BAD_ESCAPE              = OFFSET + 7;

    static {
        registerCode(JSON_PREMATURE_END,             "Premature end of embedded JSON object");
        registerCode(JSON_GARBAGE_AT_END,            "Garbage at end of embedded JSON object");
        registerCode(JSON_BAD_IDENTIFIER,            "Bad JSON identifier");
        registerCode(JSON_BAD_STRING,                "Bad string in JSON");
        registerCode(JSON_BAD_NUMBER,                "Malformed number in JSON");
        registerCode(JSON_SYNTAX,                    "Invalid JSON syntax");
        registerCode(JSON_BAD_ESCAPE,                "Invalid JSON (unicode) escape sequence");
    }

    public JsonException(final int errorCode, final int pos) {
        super(errorCode, String.format("At pos %d", pos));
    }

    public JsonException(final int errorCode, final String text) {
        super(errorCode, text);
    }

    public JsonException(final int errorCode) {
        this(errorCode, null);
    }

}
