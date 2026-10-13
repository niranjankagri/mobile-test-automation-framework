package com.mobile.automation.core;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Class to read Environment Variables
 *
 */
public class ConfigReader {
    public static Logger logger = LogManager.getLogger(ConfigReader.class.getName());

    /**
     * Gets the environment variable if set, other wise returns the override
     * 
     * @param envVarName
     * @param override
     * @return
     */
    public static String getConfiguration(String envVarName, final String override) {
        // get value from VM arguments i.e -DenvVarName=value
        String value = System.getProperty(envVarName);
        if (value == null || value.length() == 0) {
            logger.info("Property either not set or empty: " + envVarName + ", returning override value: " + override);
            return override;
        }
        logger.info("Property is set: " + envVarName + ", with value: " + value);
        return value;
    }
}
