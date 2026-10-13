package com.mobile.automation.ui.selenium;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.appium.java_client.AppiumDriver;

/**
 * Base class of every page object (through its element repository class).
 *
 * Inherits all element actions (click, type, waits, getText ...) from
 * {@link DriverActions} and adds test-data helpers that any screen can use.
 * Screen-specific actions belong in the page classes in
 * com.mobile.automation.pagehelpermethods.
 */
public class BasePage extends DriverActions {

    // Test data loaded by loadProperties()
    protected Properties properties = new Properties();

    /**
     * @param driver the running session
     */
    public BasePage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Loads resources/testfiles/&lt;fileName&gt;.properties into {@link #properties}.
     * A missing file is logged, not thrown, so a page can still be used without it.
     *
     * @param fileName file name without the .properties extension
     */
    public void loadProperties(String fileName) {
        String path = System.getProperty("user.dir") + "/resources/testfiles/" + fileName + ".properties";
        // try-with-resources closes the file as soon as it is read
        try (InputStream in = new FileInputStream(path)) {
            properties.load(in);
        } catch (IOException ex) {
            logger.error("Could not load test data " + path, ex);
        }
    }

    /**
     * @param key a key of the loaded test data
     * @return String its value, or null when the key is not there
     */
    public String getKeyValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * Kept for existing callers: loadProperties() now closes the file itself,
     * so there is nothing left to close.
     */
    public void closeProperties() {
    }

}
