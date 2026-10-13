package com.mobile.automation.pagehelpermethods.ios;

import com.mobile.automation.elementrepository.ios.CalculatorPageFactory;
import com.mobile.automation.pagehelpermethods.Calculator;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.support.PageFactory;

/**
 * Actions on the calculator screen on iOS, the iOS side of {@link Calculator}.
 *
 * Same methods as the Android page, so CalculatorTest runs unchanged on iOS; only
 * the locators in {@link CalculatorPageFactory} differ. Returned by
 * IOS.getCalculatorPage() when the suite's platform parameter is "iOS".
 */
public class CalculatorPage extends CalculatorPageFactory implements Calculator {

	/**
	 * @param driver the running iOS session
	 */
	public CalculatorPage(AppiumDriver driver) {
		super(driver);
		// Fills the @iOSXCUITFindBy fields of CalculatorPageFactory with lazy element proxies
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

	/** Taps "=". */
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
