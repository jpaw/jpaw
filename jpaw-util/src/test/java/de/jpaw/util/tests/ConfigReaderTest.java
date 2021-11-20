package de.jpaw.util.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;

public class ConfigReaderTest {

    @Test
    public void testProperties() throws Exception {
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getDefaultJpawConfigReader();

        Assertions.assertNull(cfgReader.getProperty("wedonothavethis"), "Did not expect a missing prperty to be non null");
        Assertions.assertEquals("string1", cfgReader.getProperty("jpaw.testvalue"), "string check");
        Assertions.assertEquals(Boolean.TRUE, cfgReader.getProperty("testbool"), "boolean check");
        Assertions.assertEquals(46, cfgReader.getProperty("jpaw.testnum"), "numeric check");
    }
}
