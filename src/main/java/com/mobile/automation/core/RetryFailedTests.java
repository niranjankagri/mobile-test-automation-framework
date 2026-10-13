package com.mobile.automation.core;

import org.testng.*;

/**
 * 
 * Any test execution that fails will be re-executed. This class holds the logic
 * to retry a failed test Variable {@link #maxRetryCnt} holds the maximum times
 * a test would be retried
 * 
 */
public class RetryFailedTests implements IRetryAnalyzer {

    enum Browser {
        CHROME, FIREFOX, EDGEC,
    };

    private static int maxRetryCnt = 1;
    private int[] brRetryCount = new int[7];

    /**
     * Re-try running failed tests. Retry happens per browser
     * 
     * @param result
     * @return boolean
     */
    public boolean retry(ITestResult result) {
        ITestContext iContext = result.getTestContext();
        String br = getBrowser(iContext);
        // removeSkippedTest(result);
        if (br.equalsIgnoreCase("chrome")) {
            if (brRetryCount[RetryFailedTests.Browser.CHROME.ordinal()] < maxRetryCnt) {
                brRetryCount[RetryFailedTests.Browser.CHROME.ordinal()]++;
                System.out.println("Retrying " + result.getName() + " and the count is " + brRetryCount[RetryFailedTests.Browser.CHROME.ordinal()]);
                return true;
            }

        } else if (br.equalsIgnoreCase("firefox")) {
            if (brRetryCount[RetryFailedTests.Browser.FIREFOX.ordinal()] < maxRetryCnt) {
                brRetryCount[RetryFailedTests.Browser.FIREFOX.ordinal()]++;
                System.out.println("Retrying " + result.getName() + " and the count is " + brRetryCount[RetryFailedTests.Browser.FIREFOX.ordinal()]);
                return true;
            }

        } else if (br.equalsIgnoreCase("edgec")) {
            if (brRetryCount[RetryFailedTests.Browser.EDGEC.ordinal()] < maxRetryCnt) {
                brRetryCount[RetryFailedTests.Browser.EDGEC.ordinal()]++;
                System.out.println("Retrying " + result.getName() + " and the count is " + brRetryCount[RetryFailedTests.Browser.EDGEC.ordinal()]);
                return true;
            }
        } else {
            System.out.println(" Browser not in enum, not supported");
            return false;
        }

        return false;
    }

    /**
     * Get browser from ITestContext
     * 
     * @param context
     * @return String
     */
    private String getBrowser(ITestContext context) {
        String browser = "Chrome";
        if (context.getCurrentXmlTest().getParameter("browser") != null) {
            browser = context.getCurrentXmlTest().getParameter("browser");
        }
        return browser;
    }

}
