package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.Calculator;
import com.mobile.automation.pagehelpermethods.ios.CalculatorPage;

import io.appium.java_client.AppiumDriver;

/**
 * iOS implementation of {@link Platform}: hands out the iOS page objects.
 *
 * Used when the suite's platform parameter is "iOS" (CalculatorTestSuite-iOS.xml).
 * The session comes from resources/config/ios.properties. Not run yet: iOS needs a Mac.
 */
public class IOS implements Platform {

	// Session started by DriverFactory.getIosInstance()
	private final AppiumDriver driver;

	/**
	 * @param driver the running iOS session
	 */
	public IOS(AppiumDriver driver) {
		this.driver = driver;
	}

	/**
	 * A new page object per call, so its elements are looked up on the current screen.
	 */
	@Override
	public Calculator getCalculatorPage() {
		return new CalculatorPage(driver);
	}

}
