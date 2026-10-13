package com.mobile.automation.elementrepository.ios;

import org.openqa.selenium.WebElement;

import com.mobile.automation.ui.selenium.BasePage;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

/**
 * Element repository of the calculator screen on iOS: locators only.
 *
 * NOT VERIFIED YET: no iOS run has been possible (it needs a Mac). The iOS simulator
 * has no calculator app, so these are example accessibility ids. Before the first
 * run, open your app in Appium Inspector (iOS session from ios.properties), tap each
 * key, and copy its "accessibility id" (or "name") here.
 *
 * Locator choice on iOS, best first:
 * <ol>
 * <li>accessibility = the element's accessibilityIdentifier (set by the app developers)</li>
 * <li>iOSNsPredicate, e.g. "type == 'XCUIElementTypeButton' AND label == '2'"</li>
 * <li>iOSClassChain, e.g. "**&#47;XCUIElementTypeButton[`label == '2'`]"</li>
 * <li>xpath: works, but is slow on iOS</li>
 * </ol>
 */
public class CalculatorPageFactory extends BasePage {

	/**
	 * @param driver the running iOS session
	 */
	public CalculatorPageFactory(AppiumDriver driver) {
		super(driver);
	}

	// Example ids: replace with the ones Appium Inspector shows for your app
	@iOSXCUITFindBy(accessibility = "2")
	protected WebElement digit_two;

	@iOSXCUITFindBy(accessibility = "4")
	protected WebElement digit_four;

	@iOSXCUITFindBy(accessibility = "+")
	protected WebElement add;

	@iOSXCUITFindBy(accessibility = "=")
	protected WebElement equals;

	// The text field or label that shows the answer after "="
	@iOSXCUITFindBy(accessibility = "result")
	protected WebElement results;

}
