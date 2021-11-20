package de.jpaw.util.tests;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.api.ConfigurationReader;
import de.jpaw.util.ConfigurationReaderFactory;

public class ConfigReaderTest {

    @Test
    public void testProperties() throws Exception {
        final Path resourceDirectory = Paths.get("src","test","resources");
        final String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/jpaw.properties";
        final ConfigurationReader cfgReader = ConfigurationReaderFactory.getConfigReader(absolutePath);

        Assertions.assertNull(cfgReader.getProperty("wedonothavethis"), "Did not expect a missing prperty to be non null");
        Assertions.assertEquals("string1", cfgReader.getProperty("jpaw.testvalue"), "string check");
        Assertions.assertEquals(Boolean.TRUE, cfgReader.getBooleanProperty("testbool"), "boolean check");
        Assertions.assertEquals(46, cfgReader.getIntProperty("jpaw.testnum"), "numeric check");
    }
}
