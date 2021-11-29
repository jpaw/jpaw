package de.jpaw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.api.ConfigurationReader;

/** Instances of this class store a cached property file and return configuration values. */
public class ConfigurationReaderInstance implements ConfigurationReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReaderInstance.class);

    private final Properties props;

    /**
     * Creates an instance of a configuration reader without a fallback to a properties file.
     * This constructor only evaluates environment variables and system properties.
     */
    public ConfigurationReaderInstance() {
        props = null;
    }

    /**
     * Creates an instance of a configuration reader without a fallback to a properties file.
     * This constructor only evaluates environment variables and system properties.
     */
    public ConfigurationReaderInstance(InputStream is, String source, String type) throws IOException {
        props = new Properties();
        props.load(is);
        LOGGER.info("Successfully loaded properties from {} {} with {} values", type, source, props.size());
    }

    @Override
    public String getProperty(String key) {
        // initial attempt is the system property
        String properties = System.getProperty(key);
        if (properties != null) {
            return properties;
        }
        // next, try environment
        properties = System.getenv(key);
        if (properties != null) {
            return properties;
        }
        // last, if props have been loaded, try the props
        if (props != null) {
            properties = props.getProperty(key);
        }
        return properties;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        final String actualValue = getProperty(key);
        return actualValue != null ? actualValue : defaultValue;
    }

    @Override
    public Integer getIntProperty(String key) {
        final String actualValue = getProperty(key);
        if (actualValue != null) {
            return Integer.valueOf(actualValue);
        }
        return null;
    }

    @Override
    public int getIntProperty(String key, int defaultValue) {
        final Integer actualValue = getIntProperty(key);
        return actualValue != null ? actualValue : defaultValue;
    }

    @Override
    public Boolean getBooleanProperty(String key) {
        final String actualValue = getProperty(key);
        if (actualValue != null) {
            if (actualValue.equals("1"))
                return Boolean.TRUE;
            if (actualValue.equalsIgnoreCase("y"))
                return Boolean.TRUE;
            return Boolean.valueOf(actualValue);
        }
        return null;
    }

    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        final Boolean actualValue = getBooleanProperty(key);
        return actualValue != null ? actualValue : defaultValue;
    }

    @Override
    public UUID getUUIDProperty(String key) {
        final String actualValue = getProperty(key);
        if (actualValue != null) {
            return UUID.fromString(actualValue);
        }
        return null;
    }
}
