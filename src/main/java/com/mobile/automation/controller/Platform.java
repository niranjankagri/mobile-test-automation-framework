package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.Calculator;

/**
 * What a test can ask for, independent of the operating system.
 *
 * Tests only talk to this interface (through {@code BaseTestSuite.app}), so the
 * same test runs on Android or iOS: {@link PlatformController} decides which
 * implementation is behind it. Every new screen gets one getter here and one
 * implementation per platform.
 */
public interface Platform {

	/**
	 * @return Calculator the calculator screen of the app under test, with this platform's locators
	 */
	Calculator getCalculatorPage();

}
