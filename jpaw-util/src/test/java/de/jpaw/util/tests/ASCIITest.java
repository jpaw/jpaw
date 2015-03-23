package de.jpaw.util.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.util.CharTestsASCII;

/** Tests for the various CharTestASCII checks. */
public class ASCIITest {

    private void runTest(String data, boolean isDigits, boolean isUpper, boolean isLower, boolean isPrintable, boolean isPrintableOrTab) {
        Assert.assertEquals(CharTestsASCII.isDigit(data),               isDigits);
        Assert.assertEquals(CharTestsASCII.isDigitByPattern(data),      isDigits);
        Assert.assertEquals(CharTestsASCII.isUpperCase(data),           isUpper);
        Assert.assertEquals(CharTestsASCII.isUpperCaseByPattern(data),  isUpper);
        Assert.assertEquals(CharTestsASCII.isLowerCase(data),           isLower);
        Assert.assertEquals(CharTestsASCII.isLowerCaseByPattern(data),  isLower);
        Assert.assertEquals(CharTestsASCII.isPrintable(data),           isPrintable);
        Assert.assertEquals(CharTestsASCII.isPrintableByPattern(data),  isPrintable);
        Assert.assertEquals(CharTestsASCII.isPrintableOrTab(data),      isPrintableOrTab);
        Assert.assertEquals(CharTestsASCII.isPrintableOrTabByPattern(data), isPrintableOrTab);
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
