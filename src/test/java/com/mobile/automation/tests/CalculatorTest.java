package com.mobile.automation.tests;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.mobile.automation.ui.selenium.BaseTestSuite;

/**
 * Calculator checks. BaseTestSuite starts the session and sets {@code app} to the
 * platform from the suite file, so this class does not know if it runs on Android or iOS.
 */
public class CalculatorTest extends BaseTestSuite {

	/**
	 * Adds 2 and 4 with the calculator keys and checks that the result field shows 6.
	 * Runs on Android (CalculatorTestSuite.xml) and on iOS (CalculatorTestSuite-iOS.xml).
	 */
	@Test(description = "2 + 4 = shows 6")
	public void testCal() throws Exception {
		// Enter the sum key by key, as a user would
		app.getCalculatorPage().clickOnDigitTwo();
		app.getCalculatorPage().clickOnAdd();
		app.getCalculatorPage().clickOnDigitFour();
		app.getCalculatorPage().clickOnEquals();

		// TestNG assert: always checked (a Java 'assert' only runs with -ea) and reports both values
		assertEquals(app.getCalculatorPage().getResults(), "6", "result of 2 + 4");
	}

}
