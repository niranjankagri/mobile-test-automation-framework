package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.android.CalculatorPage;

import io.appium.java_client.AppiumDriver;

public class Android implements Platform {
	
	AppiumDriver driver;

	public Android(AppiumDriver driver) {
		this.driver = driver;
	}

	@Override
	public CalculatorPage getCalculatorPage() {
		return new CalculatorPage(driver);
	}

	
	
}
