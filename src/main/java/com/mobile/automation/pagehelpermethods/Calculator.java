package com.mobile.automation.pagehelpermethods;

/**
 * The calculator screen as the tests see it, the same on every platform.
 *
 * Each platform has its own page class with its own locators:
 * pagehelpermethods.android.CalculatorPage and pagehelpermethods.ios.CalculatorPage.
 * Platform.getCalculatorPage() returns the one for the platform under test, so a
 * test written against this interface runs unchanged on Android and iOS.
 *
 * To add a screen: write an interface like this one, one page class per platform
 * that implements it, and one getter in Platform.
 */
public interface Calculator {

	/** Taps the digit 2. */
	void clickOnDigitTwo();

	/** Taps the digit 4. */
	void clickOnDigitFour();

	/** Taps the plus key. */
	void clickOnAdd();

	/** Taps "=". */
	void clickOnEquals();

	/**
	 * @return String the answer shown after "="
	 */
	String getResults();
}
