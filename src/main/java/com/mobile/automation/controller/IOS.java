package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.android.CalculatorPage;

import io.appium.java_client.AppiumDriver;

/**
 * iOS implementation of {@link Platform}.
 *
 * Prepared, not implemented yet: the iOS session can be started from
 * resources/config/ios.properties, but there is no iOS page object so far.
 */
public class IOS implements Platform {

	// Session started by DriverFactory.getIosInstance(); used once iOS page objects exist
	private final AppiumDriver driver;

	/**
	 * @param driver the running iOS session
	 */
	public IOS(AppiumDriver driver) {
		this.driver = driver;
	}

	/**
	 * No iOS calculator page object yet, so there is nothing to return.
	 *
	 * @return null until an iOS page object exists
	 */
	@Override
	public CalculatorPage getCalculatorPage() {
		return null;
	}

}
