package com.mobile.automation.pagehelpermethods.android;

import org.openqa.selenium.WebDriver;
import io.appium.java_client.support.PageFactory;

import com.mobile.automation.elementrepository.android.CalculatorPageFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class CalculatorPage extends CalculatorPageFactory{

	public CalculatorPage(AppiumDriver driver) {
		super(driver);
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
	public void clickOnDigitTwo() {
		clickElement(digit_two);
	}
	
	public void clickOnDigitFour() {
		clickElement(digit_four);
	}
	
	public void clickOnAdd() {
		clickElement(add);
	}
	
	public void clickOnEquals() {
		clickElement(equals);
	}

	public String getResults() {
		return results.getText();
	}
}
