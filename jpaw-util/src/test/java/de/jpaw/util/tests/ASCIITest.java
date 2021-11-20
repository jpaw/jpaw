package de.jpaw.util.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.util.CharTestsASCII;

/** Tests for the various CharTestASCII checks. */
public class ASCIITest {

    private void runTest(String data, boolean isDigits, boolean isUpper, boolean isLower, boolean isPrintable, boolean isPrintableOrTab) {
        Assertions.assertEquals(CharTestsASCII.isDigit(data),               isDigits);
        Assertions.assertEquals(CharTestsASCII.isDigitByPattern(data),      isDigits);
        Assertions.assertEquals(CharTestsASCII.isUpperCase(data),           isUpper);
        Assertions.assertEquals(CharTestsASCII.isUpperCaseByPattern(data),  isUpper);
        Assertions.assertEquals(CharTestsASCII.isLowerCase(data),           isLower);
        Assertions.assertEquals(CharTestsASCII.isLowerCaseByPattern(data),  isLower);
        Assertions.assertEquals(CharTestsASCII.isPrintable(data),           isPrintable);
        Assertions.assertEquals(CharTestsASCII.isPrintableByPattern(data),  isPrintable);
        Assertions.assertEquals(CharTestsASCII.isPrintableOrTab(data),      isPrintableOrTab);
        Assertions.assertEquals(CharTestsASCII.isPrintableOrTabByPattern(data), isPrintableOrTab);
    }

    @Test
    public void testStrings() throws Exception {
        runTest("42",           true,  false, false, true,  true);
        runTest("Hello, world", false, false, false, true,  true);
        runTest("Hello\tWorld", false, false, false, false, true);
        runTest("hello",        false, false, true,  true,  true);
        runTest("HELLO",        false, true,  false, true,  true);
        runTest("HELLO\r\n",    false, false, false, false, false);
    }
}
