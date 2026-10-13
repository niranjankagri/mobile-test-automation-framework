package com.mobile.automation.tests;

import static org.testng.Assert.assertEquals;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.mobile.automation.ui.selenium.BaseTestSuite;

/**
 * Two tests that fail and skip ON PURPOSE, to show what the reports look like
 * for a failure and a skip. They are not part of the normal suite.
 *
 * How to run them (Appium server and emulator running, as for the normal suite):
 * <pre>
 * mvn test -Dsurefire.suiteXmlFiles=resources/suites/ReportDemoSuite.xml
 * </pre>
 * Expected result: CalculatorTest passes, failOnPurpose fails, skipOnPurpose is
 * skipped, so Maven ends with "Tests run: 3, Failures: 1, Errors: 0, Skipped: 1" and
 * BUILD FAILURE. The line above it ("Tests run: 4 ... Skipped: 2 ... in TestSuite") also
 * counts the first attempt of the failed test, which RetryFailedTests ran again.
 *
 * Where to look afterwards:
 * <ul>
 * <li>target/surefire-reports/custom-emailable-report.html: the failure with its
 *     screenshot and stack trace, and the skip with its reason</li>
 * <li>test-output/extent-report.html: one entry per class with PASS / FAIL / SKIP</li>
 * <li>resources/screenshots/failOnPurpose.png: the device screen at the failure</li>
 * </ul>
 */
public class ReportDemoTest extends BaseTestSuite {

	/**
	 * Fails on purpose: same steps as CalculatorTest, but expects 7.
	 * Shows a failure with screenshot, retry and stack trace in the reports.
	 */
	@Test(description = "Fails on purpose: 2 + 4 is checked against 7 to show a failure in the reports")
	public void failOnPurpose() throws Exception {
		// Same steps as the real calculator test, so the screenshot shows a real screen
		app.getCalculatorPage().clickOnDigitTwo();
		app.getCalculatorPage().clickOnAdd();
		app.getCalculatorPage().clickOnDigitFour();
		app.getCalculatorPage().clickOnEquals();

		// Wrong on purpose: the app shows 6. The listener saves a screenshot, RetryFailedTests
		// runs the test once more, then it is reported as failed with "expected [7] but found [6]"
		assertEquals(app.getCalculatorPage().getResults(), "7", "demo failure, 2 + 4 checked against 7");
	}

	/**
	 * Skipped on purpose: shows a skipped test and its reason in the reports.
	 */
	@Test(description = "Skipped on purpose: shows how a skipped test and its reason appear in the reports")
	public void skipOnPurpose() {
		// SkipException marks the test as skipped (not failed) and its message becomes the reason.
		// Real tests use it when a precondition is missing, e.g. a feature not on this platform.
		throw new SkipException("Skipped on purpose to show a skipped test in the reports");
	}

}
