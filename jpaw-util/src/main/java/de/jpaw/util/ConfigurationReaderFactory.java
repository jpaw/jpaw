package de.jpaw.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurationReaderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReaderFactory.class);

    public static final String DEFAULT_JPAW_PROPERTIES = "&jpaw.properties";

    private static final ConfigurationReaderInstance FALLBACK_READER = new ConfigurationReaderInstance();

    /** A cache to keep all instances of the readers. */
    private static final Map<String, ConfigurationReaderInstance> INSTANCES = new ConcurrentHashMap<>();

    private ConfigurationReaderFactory() { }

    private static ConfigurationReaderInstance getConfigReaderFromPath(final String path) {
        try (final FileInputStream fis = new FileInputStream(path)) {
            return new ConfigurationReaderInstance(fis, path, "file");
        } catch (Exception e) {
            LOGGER.error("Error obtaining properties from file {}: {} {}", path, e.getMessage(), ExceptionUtil.causeChain(e));
            return FALLBACK_READER;
        }
    }

    private static ConfigurationReaderInstance getConfigReaderFromResource(final String path) {
        try (final InputStream is = ConfigurationReaderFactory.class.getResourceAsStream(path.substring(1))) {
            return new ConfigurationReaderInstance(is, path, "resource");
        } catch (Exception e) {
            LOGGER.error("Error obtaining properties from resource {}: {} {}", path, e.getMessage(), ExceptionUtil.causeChain(e));
            return FALLBACK_READER;
        }
    }

    public static ConfigurationReaderInstance getConfigReader(final String propertiesFilePath) {
        if (propertiesFilePath == null || propertiesFilePath.isEmpty()) {
            // get instance without property file
            return INSTANCES.computeIfAbsent("", path -> new ConfigurationReaderInstance());
        }
        return INSTANCES.computeIfAbsent(propertiesFilePath, path -> {
            final char firstChar = path.charAt(0);
            if (firstChar == '~') {
                // expand ~ by user's home
                return getConfigReaderFromPath(System.getProperty("user.home") + path.substring(1));
            }
            if (firstChar == '&') {
                // read from resource
                return getConfigReaderFromResource(path.substring(1));
            }
            // read from unmodified filename
            return getConfigReaderFromPath(path);
        });
    }

    public static ConfigurationReaderInstance getDefaultJpawConfigReader() {
        return getConfigReader(DEFAULT_JPAW_PROPERTIES);
    }
}
