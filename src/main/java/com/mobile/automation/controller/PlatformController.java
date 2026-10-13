package com.mobile.automation.controller;

import io.appium.java_client.AppiumDriver;

public class PlatformController {
	
	AppiumDriver driver;
	
	public PlatformController(AppiumDriver driver) {
		this.driver = driver;
	}

	public Platform getPlatform(String platform) {
		if (platform.equalsIgnoreCase("android")) {
			return new Android(driver);
		} else if (platform.equalsIgnoreCase("ios")) {
			return new IOS(driver);
		}
		return null;
	}

}
