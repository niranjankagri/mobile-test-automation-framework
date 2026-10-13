package com.mobile.automation.controller;

import io.appium.java_client.AppiumDriver;

/**
 * Picks the {@link Platform} implementation for the "platform" parameter of the
 * TestNG suite (Android or iOS). Called once per test class by BaseTestSuite.
 */
public class PlatformController {

	// Session started by DriverFactory for the same platform
	private final AppiumDriver driver;

	/**
	 * @param driver the running session, passed on to the platform's page objects
	 */
	public PlatformController(AppiumDriver driver) {
		this.driver = driver;
	}

	/**
	 * @param platform "android" or "ios" (any case), from the suite file
	 * @return Platform the matching implementation
	 * @throws IllegalArgumentException for any other value, instead of returning
	 *                                  null and failing later with a NullPointerException
	 */
	public Platform getPlatform(String platform) {
		if (platform.equalsIgnoreCase("android")) {
			return new Android(driver);
		} else if (platform.equalsIgnoreCase("ios")) {
			return new IOS(driver);
		}
		throw new IllegalArgumentException("Unknown platform '" + platform + "', expected Android or iOS");
	}

}
