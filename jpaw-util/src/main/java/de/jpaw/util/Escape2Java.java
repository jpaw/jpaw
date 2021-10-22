package de.jpaw.util;

public final class Escape2Java {
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    private Escape2Java() { }

    public static String escapeString2Java(final String s) {
        if (!needsEscaping(s))
            return s;  // shortcut - avoid buffer allocation unless required
        final StringBuilder sb = new StringBuilder(s.length() * 2);  // rough estimate on size
        for (int i = 0; i < s.length(); ++i) {
            int c = 0xffff & s.charAt(i);
            if (c < 0x20) {
                switch (c) {
                case '\b':
                    sb.append('\\');
                    sb.append('b');
                    break;
                case '\t':
                    sb.append('\\');
                    sb.append('t');
                    break;
                case '\n':
                    sb.append('\\');
                    sb.append('n');
                    break;
                case '\f':
                    sb.append('\\');
                    sb.append('f');
                    break;
                case '\r':
                    sb.append('\\');
                    sb.append('r');
                    break;
                default:
//                    sb.append(String.format("\\%o", c));      // 1 or 2 octal digits
                    //sb.append(String.format("\\u%04x", c));        // 4 hex digits - Apache commons compatibility
                    appendHex(sb, c);
                    break;
                }
            } else if (c > 0x7e) {
                // Unicode escape
//                sb.append(String.format("\\u%04x", c));        // 4 hex digits
                appendHex(sb, c);
            } else {
                // printable ASCII character
                switch (c) {
//                case '\'':  -- not done in Apache commons
                case '\"':
                case '\\':
                    sb.append('\\');
                    sb.append((char)c);
                    break;
                default:
                    sb.append((char)c);
                    break;
                }
            }
        }
        return sb.toString();
    }

    private static void appendHex(StringBuilder sb, int c) {
        sb.append('\\');
        sb.append('u');
        sb.append(HEX_DIGITS[0xf & (c >> 12)]);
        sb.append(HEX_DIGITS[0xf & (c >>  8)]);
        sb.append(HEX_DIGITS[0xf & (c >>  4)]);
        sb.append(HEX_DIGITS[0xf &  c]);
    }

    // return false if the string contains a non-ASCII printable character, else true
    private static boolean needsEscaping(final String s) {
        if (s != null) {
            for (int i = 0; i < s.length(); ++i) {
                int c = s.charAt(i);
                if (c < 0x20 || c > 0x7e)
                    return true;
                switch (c) {
                case '\'':
                case '\"':
                case '\\':
                    return true;
                }
            }
        }
        return false;
    }
}
