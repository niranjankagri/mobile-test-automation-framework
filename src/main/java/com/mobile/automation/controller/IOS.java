package com.mobile.automation.controller;

import com.mobile.automation.pagehelpermethods.android.CalculatorPage;

import io.appium.java_client.AppiumDriver;

public class IOS implements Platform {
	
	AppiumDriver driver;

	public IOS(AppiumDriver driver) {
		this.driver = driver;
	}

	@Override
	public CalculatorPage getCalculatorPage() {
		return null;
	}
	
	

}
