package de.jpaw.util;

/**
 * Escapes control characters in a string, to allow editing of serialized Bonaportable objects for example.
 */
public final class StringSerializer {

    private StringSerializer() { }

    /**
     * The escape char to display the bonaportable control chars
     */
    protected static final char ESC = '\\';

    /**
     * Converts a bonaPortable provided with a Stringbuilder to a simple string representation. All bonaportable control characters, tabs and backslashes are
     * converted to escaped chars.
     *
     * @param builder
     *            A Stringbuilder that containes a bonaportable
     * @return the converted bonaportable
     */
    public static String toString(StringBuilder builder) {
        StringBuilder result = new StringBuilder();
        for (char c : builder.toString().toCharArray()) {
            if (c == '\t') {
                // special handling for tabs that only appear in ascii/unicode fields. We want java notation
                result.append("\\t");
            } else if (c < 32) {
                // all chars 0..31 are control chars, prefix with \ and shift value to letter space (+64)
                result.append('\\').append((char) (c + 64));
            } else if (c == '\\') {
                // escape the escape char
                result.append("\\\\");
            } else {
                // leave everything else untouched
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts a bonaPortable provided with a simple String representation to Stringbuilder.
     */
    public static StringBuilder fromString(String string) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (char c : string.toCharArray()) {
            if (escaped) {
                escaped = false;
                if (c == 't') {
                    // insert a tab
                    result.append('\t');
                } else if (c == '\\') {
                    // insert a backslash
                    result.append(c);
                } else {
                    // insert a control char
                    result.append((char) (c - 64));
                }
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        return result;
    }

    public static String altToString(String src) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < src.length()) {
            char c = src.charAt(i);
            if (c < 32) {
                // escaped
                result.append('<').append((char) (c + 64)).append('>');
            } else {
                // leave everything else untouched
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts a bonaPortable provided with a simple String representation to Stringbuilder.
     */
    public static String altFromString(String src) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < src.length()) {
            char c = src.charAt(i);
            if (c == '<' && i < src.length() - 2 && src.charAt(i + 2) == '>') {
                // escaped?
                c = src.charAt(i + 1);
                if (c >= '@' && c < 96) {
                    result.append((char)(c - 64));
                    i += 3;
                } else {
                    result.append(src.charAt(i));
                    ++i;
                }
            } else {
                result.append(src.charAt(i));
                ++i;
            }
        }
        return result.toString();
    }
}
