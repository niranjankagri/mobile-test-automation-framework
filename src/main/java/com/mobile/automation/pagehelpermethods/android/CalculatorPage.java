package com.mobile.automation.pagehelpermethods.android;

import com.mobile.automation.elementrepository.android.CalculatorPageFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.support.PageFactory;

/**
 * Actions on the Google Calculator screen (Android).
 *
 * Tests call these methods and never touch locators; the locators are in
 * {@link CalculatorPageFactory}. The clicks go through DriverActions, which waits
 * for the element and logs every step.
 */
public class CalculatorPage extends CalculatorPageFactory {

	/**
	 * @param driver the running Android session
	 */
	public CalculatorPage(AppiumDriver driver) {
		super(driver);
		// Fills the @AndroidFindBy fields of CalculatorPageFactory with lazy element proxies
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	/** Taps the digit 2. */
	public void clickOnDigitTwo() {
		clickElement(digit_two);
	}

	/** Taps the digit 4. */
	public void clickOnDigitFour() {
		clickElement(digit_four);
	}

	/** Taps the plus key. */
	public void clickOnAdd() {
		clickElement(add);
	}

	/** Taps "=", which moves the answer into the result field. */
	public void clickOnEquals() {
		clickElement(equals);
	}

	/**
	 * @return String the answer shown after "=", without surrounding spaces
	 */
	public String getResults() {
		return getText(results);
	}
}
