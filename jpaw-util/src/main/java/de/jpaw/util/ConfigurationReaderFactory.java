package de.jpaw.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurationReaderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReaderFactory.class);

    public static final String DEFAULT_JPAW_PROPERTIES = "&./jpaw.properties";

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
            if (is == null) {
                // null is a valid response, if the resource does not exist
                LOGGER.warn("Resource {} does not exist, using fallback configuration (environment / system property only)", path);
                return FALLBACK_READER;
            }
            return new ConfigurationReaderInstance(is, path, "resource");
        } catch (Exception e) {
            LOGGER.error("Error obtaining properties from resource {}: {} {}", path, e.getMessage(), ExceptionUtil.causeChain(e));
            return FALLBACK_READER;
        }
    }

    /** Returns a configuration reader which uses environment or system properties only. */
    public static ConfigurationReaderInstance getEnvOrSystemPropConfigReader() {
        return FALLBACK_READER;
    }

    /** Returns a configuration reader which uses a property file as specified by a system property. */
    public static ConfigurationReaderInstance getConfigReaderForName(String propertyFileName, String defaultPath) {
        final String realPropertiesPath = FALLBACK_READER.getProperty(propertyFileName, defaultPath);
        final String currentWorkingDir = Paths.get("").toAbsolutePath().toString();
        if (realPropertiesPath == null) {
            // no default path specified, and no property set, use fallback reader
            LOGGER.info("Using env/system property configuration for {} (path is unspecified and no default given)", propertyFileName);
            return FALLBACK_READER;
        }
        LOGGER.info("Reading {} from path {} (working dir is {})", propertyFileName, realPropertiesPath, currentWorkingDir);
        return getConfigReader(realPropertiesPath);
    }

    /** Returns a configuration reader which uses a resource or file as fallback. */
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
        final String jpawPropertiesPath = FALLBACK_READER.getProperty("jpaw.properties", DEFAULT_JPAW_PROPERTIES);
        LOGGER.info("Reading jpaw.properties from path {}", jpawPropertiesPath);
        return getConfigReader(jpawPropertiesPath);
    }
}
