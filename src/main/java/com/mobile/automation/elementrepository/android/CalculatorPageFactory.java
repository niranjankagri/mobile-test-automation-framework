package com.mobile.automation.elementrepository.android;

import org.openqa.selenium.WebElement;

import com.mobile.automation.ui.selenium.BasePage;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

/**
 * Element repository of the Google Calculator screen (Android): locators only.
 *
 * The actions on these elements live in {@code CalculatorPage}, which extends this
 * class and lets Appium's PageFactory fill the fields. Each field is a proxy that
 * looks the element up again on every use, so it never goes stale.
 * IDs were checked against Google Calculator 9.3 with a uiautomator dump.
 */
public class CalculatorPageFactory extends BasePage {

	/**
	 * @param driver the running Android session
	 */
	public CalculatorPageFactory(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(id = "com.google.android.calculator:id/digit_2")
	protected WebElement digit_two;

	@AndroidFindBy(id = "com.google.android.calculator:id/digit_4")
	protected WebElement digit_four;

	@AndroidFindBy(id = "com.google.android.calculator:id/op_add")
	protected WebElement add;

	@AndroidFindBy(id = "com.google.android.calculator:id/eq")
	protected WebElement equals;

	// After "=" the answer moves out of the formula field into result_final
	@AndroidFindBy(id = "com.google.android.calculator:id/result_final")
	protected WebElement results;

}
