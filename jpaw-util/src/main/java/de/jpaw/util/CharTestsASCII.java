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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *          This class defines a couple of simple tests for the Java primitive type {@code char} which correspond to the
 *          macros included in the header file ctype.h for the programming
 *          language C. These macros really should have been included in the
 *          standard Java class "Character", but that class seems to focus more
 *          on localization dependent tests rather than file processing.
 *          There is an implementation in apache.commons.lang.CharUtils,
 *          but for such a small functionality we avoid an external dependency and
 *          create the required methods here.
 *
 * @author Michael Bischoff
 *
 * Changes:
 * 1.2.1:   changed 0x7f to be no longer considered as a "printable" character, in order to be consistent with the Java patterns and common expectation
 */

public class CharTestsASCII {
    public static final Pattern UPPERCASE_PATTERN        = Pattern.compile("\\A[A-Z]*\\z");             // tests if a field consists of uppercase characters only
    public static final Pattern LOWERCASE_PATTERN        = Pattern.compile("\\A[a-z]*\\z");             // tests if a field consists of lowercase characters only
    public static final Pattern DIGIT_PATTERN            = Pattern.compile("\\A[0-9]*\\z");             // tests if a field consists of digits characters only
    public static final Pattern PRINTABLE_PATTERN        = Pattern.compile("\\A[\\x20-\\x7e]*\\z");     // tests if a field consists of printable ASCII characters only
    public static final Pattern PRINTABLE_OR_TAB_PATTERN = Pattern.compile("\\A[\\x20-\\x7e\t]*\\z");   // tests if a field consists of printable ASCII characters or TABs only

    // ID map:
    // 0 = lowercase
    // 1 = uppercase
    // 2 = digit
    // 3 = other identifier in Javascript
    // 9 = anything else
    // index ranges from 0x20 to 0x7f
    private static final byte CHAR_TYPE [] = {
        9, 9, 9, 9, 3, 9, 9, 9,  9, 9, 9, 9, 9, 9, 9, 9,
        2, 2, 2, 2, 2, 2, 2, 2,  2, 2, 9, 9, 9, 9, 9, 9,
        9, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 9, 9, 9, 9, 3,
        9, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 9, 9, 9, 9, 9
    };
    // ID map:
    // 0 = no number
    // 1 = integral number (digits, + / -, ., e / E)
    private static final byte NUMBER_TYPE [] = {        // digits, +, -, . and e / E
        0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 1, 0, 1, 1, 0,
        1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 1, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0
    };
    
    /**
     * The constructor is defined as private, in order to prevent that anyone
     * instantiates this class, which is not meaningful, because it contains
     * only static methods.
     */
    private CharTestsASCII() {
    }

    /** Returns true if the whole String contains ASCII upper case characters only, else false. */
    public static boolean isUpperCase(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if the whole String contains ASCII lower case characters only, else false. */
    public static boolean isLowerCase(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if the whole String contains ASCII printable characters only (range 0x20 .. 0x7e), else false. */
    public static boolean isPrintable(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiPrintable(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if the whole String contains ASCII printable characters or TABs only (range 0x20 .. 0x7e and 0x09), else false. */
    public static boolean isPrintableOrTab(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiPrintableOrTab(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if the whole String contains ASCII digits only, else false. */
    public static boolean isDigit(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <code>isAsciiPrintable()</code> tests if a character is a US-ASCII (7
     * bit) printable character, which mainly means that such a character is
     * available in every character encoding, no matter if single byte or
     * multi-byte.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents a printable ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiPrintable(char c) {
        return (c >= 0x20) && (c <= 0x7e);
    }

    /**
     * <code>isAsciiPrintable()</code> tests if a character is a US-ASCII (7
     * bit) printable character, or a tab character, which mainly means that such a character is
     * available in every character encoding, no matter if single byte or
     * multi-byte.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents a printable ASCII
     *         character or a TAB, <code>false</code> otherwise.
     */
    public static boolean isAsciiPrintableOrTab(char c) {
        return ((c >= 0x20) && (c <= 0x7e)) || (c == '\t');
    }

    /**
     * <code>isAsciiUpperCase()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English upper case character
     * <code>(A .. Z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiUpperCase(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    /**
     * <code>isAsciiLetter()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English upper case character
     * <code>(A .. Z)</code> or lower case character <code>(a .. z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case ASCII
     *         character or lower case character <code>(a .. z)</code>, <code>false</code> otherwise.
     */
    public static boolean isAsciiLetter(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    /**
     * <code>isAsciiAlnum()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English upper case character
     * <code>(A .. Z)</code> or lower case character <code>(a .. z)</code> or a digit <code>(0 .. 9)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case or lower case ASCII letter or a digit, <code>false</code> otherwise.
     */
    public static boolean isAsciiAlnum(char c) {
        return (c >= ' ') && (c <= 0x7f) && CHAR_TYPE[c - ' '] < 3;
    }

    /**
     * <code>isJavascriptIdChar()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English upper case character
     * <code>(A .. Z)</code> or lower case character <code>(a .. z)</code> or a digit <code>(0 .. 9)</code> or a dollar or an underscore.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case or lower case ASCII letter or a digit, or dollar or underscore, <code>false</code> otherwise.
     */
    public static boolean isJavascriptIdChar(char c) {
        return (c >= ' ') && (c <= 0x7f) && CHAR_TYPE[c - ' '] <= 3;
    }

    /**
     * <code>isJavascriptNumberChar()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents a valid character inside a Javascript / JSON number.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case or lower case ASCII letter or a digit, or dollar or underscore, <code>false</code> otherwise.
     */
    public static boolean isJavascriptNumberChar(char c) {
        return (c >= ' ') && (c <= 0x7f) && NUMBER_TYPE[c - ' '] != 0;
    }

    /**
     * <code>isAsciiLowerCase()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English lower case character
     * <code>(a .. z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an lower case ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiLowerCase(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    /**
     * <code>isAsciiDigit()</code> tests if a character is a US-ASCII (7 bit)
     * printable character, and represents a valid digit <code>(0 .. 9)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents a digit,
     *         <code>false</code> otherwise.
     */
    public static boolean isAsciiDigit(char c) {
        return (c >= '0') && (c <= '9');
    }


    // some redundant implementations, for comparison (GC overhead / execution time) and preference purposes

    /** Returns true if the whole String contains ASCII upper case characters only, else false. */
    public static boolean isUpperCaseByPattern(String s) {
        Matcher m = UPPERCASE_PATTERN.matcher(s);
        return m.find();
    }

    /** Returns true if the whole String contains ASCII lower case characters only, else false. */
    public static boolean isLowerCaseByPattern(String s) {
        Matcher m = LOWERCASE_PATTERN.matcher(s);
        return m.find();
    }

    /** Returns true if the whole String contains ASCII digits only, else false. */
    public static boolean isDigitByPattern(String s) {
        Matcher m = DIGIT_PATTERN.matcher(s);
        return m.find();
    }

    /** Returns true if the whole String contains ASCII printable characters only (range 0x20 .. 0x7e), else false. */
    public static boolean isPrintableByPattern(String s) {
        Matcher m = PRINTABLE_PATTERN.matcher(s);
        return m.find();
    }

    /** Returns true if the whole String contains ASCII printable characters or TABs only (range 0x20 .. 0x7e and 0x09), else false. */
    public static boolean isPrintableOrTabByPattern(String s) {
        Matcher m = PRINTABLE_OR_TAB_PATTERN.matcher(s);
        return m.find();
    }
}
