package com.mobile.automation.tests;

import org.testng.annotations.Test;

import com.mobile.automation.ui.selenium.BaseTestSuite;

public class CalculatorTest extends BaseTestSuite {
	
	@Test
	public void testCal() throws Exception {
		
		app.getCalculatorPage().clickOnDigitTwo();
		app.getCalculatorPage().clickOnAdd();
		app.getCalculatorPage().clickOnDigitFour();
		app.getCalculatorPage().clickOnEquals();
		assert app.getCalculatorPage().getResults().equals("6"):"Actual value is : " + app.getCalculatorPage().getResults() + " did not match with expected value: 6";

	}

}
