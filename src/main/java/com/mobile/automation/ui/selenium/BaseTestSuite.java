package com.mobile.automation.ui.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.mobile.automation.controller.Platform;
import com.mobile.automation.controller.PlatformController;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import io.appium.java_client.AppiumDriver;

/**
 * Base class of every test class: the life cycle of the Appium session and the reports.
 *
 * Flow per test class:
 * <ol>
 * <li>{@link #setUp(String)} starts the session for the suite's "platform" parameter
 *     (DriverFactory) and sets {@link #app} to the matching {@link Platform}.</li>
 * <li>The tests use {@code app.getXxxPage()} and never create drivers themselves.</li>
 * <li>{@link #tearDown()} ends the session, even when a test failed.</li>
 * </ol>
 * The Extent report is opened once per suite and gets one entry per test class.
 * The log4j ThreadContext key "RoutingKey" carries the platform name, so log lines
 * can be told apart per platform.
 */
public class BaseTestSuite {
    protected static AppiumDriver driver;
    protected Logger logger = LogManager.getLogger(BaseTestSuite.class.getName());
    protected static final String THREADCONTEXT_ROUTINGKEY = "RoutingKey";
    // Read by TestListener to log PASS/FAIL/SKIP into the current class's report entry
    public static ExtentTest extentTest;
    public static ExtentReports extentReports;
    // Entry point for tests: the page objects of the platform under test
    public static Platform app;

	/**
	 * Starts the Appium session for the platform and picks its page objects.
	 *
	 * @param platform "Android" or "iOS", from the TestNG suite file
	 * @throws Exception when the session cannot be created (no server, device or app)
	 */
	@Parameters({"platform"})
	@BeforeClass (alwaysRun = true)
	public void setUp(String platform) throws Exception{
		// Set here too: when classes run in parallel, their threads do not inherit the
		// ThreadContext set in @BeforeTest, and the log lines of this method need the platform
	    ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
	    if (platform.equalsIgnoreCase("android")) {
			driver = DriverFactory.getAndroidInstance();
			logger.info("Started the Android session");
		} else if (platform.equalsIgnoreCase("ios")) {
			driver = DriverFactory.getIosInstance();
			logger.info("Started the iOS session");
		} else {
			throw new IllegalArgumentException("Unknown platform '" + platform + "', expected Android or iOS");
		}

	    PlatformController controller = new PlatformController(driver);
	    app = controller.getPlatform(platform);
	}

	/**
	 * Opens the Extent report once for the whole suite.
	 */
	@BeforeSuite
	public void beforeSuiteSetUp() {
        extentReports = new ExtentReports(System.getProperty("user.dir") +"/test-output/extent-report.html");
    }

	/**
	 * Starts one report entry per test class, named after the class.
	 */
	@BeforeClass
    public void beforeClassSetUp() {
	    // 'this' is the running test class, so its name is always the right one
	    extentTest = extentReports.startTest(getClass().getSimpleName());
    }

	/**
	 * Writes the Extent report to disk at the end of the suite.
	 */
	@AfterSuite
	public void afterSuiteSetUp() {
        extentReports.flush();
    }

    /**
	 * Ends the current Appium session (a native app has no window to close).
	 */
	protected void quitSession() {
		driver.quit();
	}

	/**
	 * Closes this class's report entry and ends the session, even after a failure.
	 */
	@AfterClass	(alwaysRun = true)
	public void tearDown() {
		try {
			if (extentReports != null && extentTest != null) {
				extentReports.endTest(extentTest);
			}
			if (driver != null) {
				driver.quit();
			}
		} catch (Exception e) {
			logger.error("Could not end the session cleanly", e);
		} finally {
		    ThreadContext.clearMap();
        }
	}

    /**
     * Sets the platform as log RoutingKey before the &lt;test&gt; starts, so log lines
     * written before any test class exists (e.g. by TestListener) carry it too.
     *
     * @param platform "Android" or "iOS", from the TestNG suite file
     */
    @BeforeTest(alwaysRun = true)
    @Parameters({ "platform" })
    public void beforeTestThreadContextSetup(String platform) {
        ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
    }

    /**
     * Clears the log ThreadContext after the &lt;test&gt;.
     */
    @AfterTest(alwaysRun = true)
    public void afterTestThreadContextCleanup() {
        ThreadContext.clearMap();
    }

    /**
     * Sets the platform as log RoutingKey before each test method: with parallel
     * methods, the worker threads do not inherit the parent thread's ThreadContext.
     *
     * @param platform "Android" or "iOS", from the TestNG suite file
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({ "platform" })
    public void beforeMethodThreadContextSetup(String platform) {
        ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
    }

    /**
     * @return AppiumDriver the running session (used by TestListener for screenshots)
     */
    public static AppiumDriver getDriver() {
        return driver;
    }
}
