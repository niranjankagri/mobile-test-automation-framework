package com.mobile.automation.ui.selenium;

/**
 * This class is for defining selenium specific property keys which can be passed to VM
 * Property can be passed to VM as -Dproperty=value
 * This class also has default values for the properties
 *
 */
public class AppiumConfigs {
    public static final String DEFAULT_BROWSER = "Chrome";
    public static final String LOG_LEVEL_PROPERTY = "log.level";
    public static final String LOG_LEVEL_DEFAULT_VALUE = "6";
    public static final String CONFIG_DIR_PROPERTY = "config.dir";
    public static final String CONFIG_DIR_DEFAULT_VALUE = "resources/config";
    public static final String WEBDRIVER_REMOTE_PROPERTY = "webdriver.isRemote";
    public static final String WEBDRIVER_REMOTE_DEFAULT_VALUE = "false";
    public static final String HUB_HOST_PROPERTY = "hub.host";
    public static final String HUB_HOST_DEFAULT_VALUE = "127.0.0.1";
    public static final String HUB_PORT_PROPERTY = "hub.port";
    public static final String HUB_PORT_DEFAULT_VALUE = "5555";
    public static final String APP_URL_PROPERTY = "app.url";
    public static final String APP_URL_DEFAULT_VALUE = "example.com/";
    public static final String WEB_LANGUAGE_PROPERTY = "language";
    public static final String WEB_LANGUAGE_DEFAULT_VALUE = "English";
    public static final String APP_LOGOUT_URL = "https://example.com/logout";
}
