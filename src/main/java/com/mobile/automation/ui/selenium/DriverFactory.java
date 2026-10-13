package com.mobile.automation.ui.selenium;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobilePlatform;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * WebDriver related functions to start various driverss
 *
 */
public class DriverFactory {
	private static Logger logger = LogManager.getLogger(DriverFactory.class.getName());
	protected static AppiumDriver driver;

	// One capabilities file per platform, e.g. resources/config/android.properties
	private static final String CONFIG_DIR = "resources/config/";
	private static final String SERVER_URL_KEY = "appium.server.url";
	private static final String CAPABILITY_PREFIX = "capability.";

	/**
	 * Get Android Driver
	 *
	 * @return AndroidDriver
	 * @throws Exception
	 */
	public static AppiumDriver getAndroidInstance() throws Exception {
		// Capabilities and server address come from resources/config/android.properties
		Properties config = loadConfig("android");

		driver = new AndroidDriver(new URL(config.getProperty(SERVER_URL_KEY)), toCapabilities(config));

		return driver;
	}

	/**
	 * Get IOS Driver
	 *
	 * @return IOSDriver
	 * @throws Exception
	 */
	public static AppiumDriver getIosInstance() throws Exception {
		File f = new File(System.getProperty("user.dir"));
		File fs = new File(f, "app-debug.apk");

		// Capabilities for Android to help set properties for the driver.
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("platformName", MobilePlatform.ANDROID);
		caps.setCapability("appium:appActivity", "com.example.app.SplashActivity");
		caps.setCapability("appium:appWaitActivity", "com.example.app.WelcomeActivity");
		caps.setCapability("appium:platformVersion", "9.0");
		caps.setCapability("appium:deviceName", "Andorid Emulator");
		caps.setCapability("appium:automationName", "UiAutomator2");
		caps.setCapability("appium:app", fs.getAbsolutePath());
		caps.setCapability("appium:newCommandTimeout", "300");
		caps.setCapability("appium:autoDismissAlerts", true);
		caps.setCapability("appium:unicodeKeyboard", true);
		caps.setCapability("appium:resetKeyboard", true);

		driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
		return driver;
	}

	/**
	 * Reads resources/config/&lt;platform&gt;.properties. A -D system property with the
	 * same key wins, so a single run can point to another device or server.
	 *
	 * @param platform android or ios
	 * @return Properties the settings for that platform
	 * @throws IOException when the file is missing
	 */
	private static Properties loadConfig(String platform) throws IOException {
		File file = new File(System.getProperty("user.dir"), CONFIG_DIR + platform + ".properties");
		Properties config = new Properties();
		try (InputStream in = new FileInputStream(file)) {
			config.load(in);
		}
		for (String key : config.stringPropertyNames()) {
			String override = System.getProperty(key);
			if (override != null && !override.isEmpty()) {
				config.setProperty(key, override);
			}
		}
		logger.info("Loaded " + file.getName() + ": " + config);
		return config;
	}

	/**
	 * Turns every capability.&lt;name&gt; entry into an Appium capability.
	 *
	 * @param config the platform settings from {@link #loadConfig(String)}
	 * @return DesiredCapabilities ready for the driver
	 */
	private static DesiredCapabilities toCapabilities(Properties config) {
		DesiredCapabilities caps = new DesiredCapabilities();
		for (String key : config.stringPropertyNames()) {
			if (!key.startsWith(CAPABILITY_PREFIX)) {
				continue;
			}
			String name = key.substring(CAPABILITY_PREFIX.length());
			String value = config.getProperty(key).trim();
			// platformName is a W3C standard capability; Appium's own ones need the "appium:" prefix
			String w3cName = name.equals("platformName") ? name : "appium:" + name;
			caps.setCapability(w3cName, typed(name, value));
		}
		return caps;
	}

	/**
	 * Properties are text, but Appium expects real booleans and numbers.
	 * The app path is made absolute so the server finds it.
	 */
	private static Object typed(String name, String value) {
		if (name.equals("app") && !value.contains("://")) {
			return new File(System.getProperty("user.dir"), value).getAbsolutePath();
		}
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(value);
		}
		// platformVersion looks like a number ("15") but Appium requires it as text
		if (value.matches("\\d+") && !name.equals("platformVersion")) {
			return Integer.parseInt(value);
		}
		return value;
	}

}
