package de.jpaw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.jpaw.api.ConfigurationReader;

/** Instances of this class store a cached property file and return configuration values. */
public class ConfigurationReaderInstance implements ConfigurationReader {
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
    public ConfigurationReaderInstance(InputStream is) throws IOException {
        props = new Properties();
        props.load(is);
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
    public Integer getIntProperty(String key, Integer defaultValue) {
        final Integer actualValue = getIntProperty(key);
        return actualValue != null ? actualValue : defaultValue;
    }

    @Override
    public Boolean getBooleanProperty(String key) {
        final String actualValue = getProperty(key);
        if (actualValue != null) {
            return Boolean.valueOf(actualValue);
        }
        return null;
    }

    @Override
    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        final Boolean actualValue = getBooleanProperty(key);
        return actualValue != null ? actualValue : defaultValue;
    }
}
