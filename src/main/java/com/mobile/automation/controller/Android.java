package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.android.CalculatorPage;

import io.appium.java_client.AppiumDriver;

/**
 * Android implementation of {@link Platform}: hands out the Android page objects.
 */
public class Android implements Platform {

	// Session started by DriverFactory.getAndroidInstance()
	private final AppiumDriver driver;

	/**
	 * @param driver the running Android session
	 */
	public Android(AppiumDriver driver) {
		this.driver = driver;
	}

	/**
	 * A new page object per call, so its elements are looked up on the current screen.
	 */
	@Override
	public CalculatorPage getCalculatorPage() {
		return new CalculatorPage(driver);
	}

}
