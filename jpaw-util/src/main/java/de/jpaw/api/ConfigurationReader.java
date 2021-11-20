package de.jpaw.api;

/**
 *
 * API for configuration readers.
 *
 */
public interface ConfigurationReader {

    /**
     * Gets a property value as a <code>String</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or null if not found
     */
    String getProperty(String key);

    /**
     * Gets a property value as a <code>String</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or defaultValue if not found
     */
    String getProperty(String key, String defaultValue);

    /**
     * Gets a property value as a <code>Integer</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or null if not found
     */
    Integer getIntProperty(String key);

    /**
     * Gets a property value as a <code>Integer</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or defaultValue if not found
     */
    int getIntProperty(String key, int defaultValue);

    /**
     * Gets a property value as a <code>Boolean</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or null if not found
     */
    Boolean getBooleanProperty(String key);

    /**
     * Gets a property value as a <code>Boolean</code>.
     * The value is retrieved from either property file (if it has been loaded), system parameter, or environment variable.
     *
     * @return the property, or defaultValue if not found
     */
    boolean getBooleanProperty(String key, boolean defaultValue);
}
