package com.mobile.automation.ui.selenium;

import java.io.File;
import java.net.URL;
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

	/**
	 * Get Android Driver
	 * 
	 * @return AndroidDriver
	 * @throws Exception
	 */
	public static AppiumDriver getAndroidInstance() throws Exception {
		// Google Calculator (com.google.android.calculator), stored in the project so the path works on every machine
		File fs = new File(System.getProperty("user.dir"), "resources/apps/calculator.apk");

		// Capabilities for Android to help set properties for the driver.
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("platformName", MobilePlatform.ANDROID);
		// Appium 2+ requires the automation engine to be named
		caps.setCapability("appium:automationName", "UiAutomator2");
		// Android 15 emulator (AVD Pixel_API_35)
		caps.setCapability("appium:platformVersion", "15");
		caps.setCapability("appium:deviceName", "emulator-5554");
		caps.setCapability("appium:app", fs.getAbsolutePath());

		// Appium 2+ serves at the root; the old /wd/hub prefix is gone
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), caps);

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

}
