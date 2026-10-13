package com.mobile.automation.elementrepository.android;

import org.openqa.selenium.WebDriver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import com.mobile.automation.ui.selenium.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class CalculatorPageFactory extends BasePage {
	
	public CalculatorPageFactory(AppiumDriver driver) {
		super(driver);
	}

	@AndroidFindBy(id = "com.google.android.calculator:id/digit_2")
	public static WebElement digit_two;
	
	@AndroidFindBy(id = "com.google.android.calculator:id/digit_4")
	public static WebElement digit_four;
	
	@AndroidFindBy(id = "com.google.android.calculator:id/op_add")
	public static WebElement add;
	
	@AndroidFindBy(id = "com.google.android.calculator:id/eq")
	public static WebElement equals;
	
	// After "=" the answer moves out of the formula field into result_final
	@AndroidFindBy(id = "com.google.android.calculator:id/result_final")
	public static WebElement results;

}
