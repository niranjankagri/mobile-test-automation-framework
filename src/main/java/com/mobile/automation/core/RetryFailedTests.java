package com.mobile.automation.core;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Runs a failed test once more before it counts as failed.
 *
 * TestListener.transform() attaches this analyzer to every test. TestNG creates
 * one analyzer object per test method, so the counter below is per test: each
 * test gets at most {@link #maxRetryCnt} extra attempts. The failed attempt is
 * reported as SKIP, which TestListener cleans up when the retry passes.
 *
 * (The older version counted per browser (Chrome/Firefox/Edge) from a "browser"
 * suite parameter; a mobile suite has none, so every test used the "Chrome"
 * counter. The behaviour, one retry per failed test, stays the same.)
 */
public class RetryFailedTests implements IRetryAnalyzer {

    // Extra attempts allowed per test
    private static int maxRetryCnt = 1;
    // Attempts used so far by the test this analyzer belongs to
    private int retryCount = 0;

    /**
     * Called by TestNG after a failure.
     *
     * @param result the failed attempt
     * @return boolean true to run the test again
     */
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCnt) {
            retryCount++;
            System.out.println("Retrying " + result.getName() + " and the count is " + retryCount);
            return true;
        }
        return false;
    }

}
