package com.mobile.automation.ui.selenium;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.ITestContext;
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
 * This function defines the core selenium actions, 
 * 		@BeforeClass - Before every class, creating a new webDriver instance
 * 		@AfterClass -  Quit the WebDriver when the test is completed. 
 * 
 * All the respective webPage related classes (which contain the functions specific to that particular webPage) are also initiated here, in the setUp function
 * 
 * This class needs to be extended, for every new automation testClass
 *
 */
public class BaseTestSuite {
    protected static AppiumDriver driver;
    protected Logger logger = LogManager.getLogger(BaseTestSuite.class.getName());
    protected static final String THREADCONTEXT_ROUTINGKEY = "RoutingKey";
    public static ExtentTest extentTest;
    public static ExtentReports extentReports;
    public static int executedClassCount = 0;
    public static Platform app;
    
	/**
	 * Create an instance of webdriver and initiate all page
	 * 
	 * @param platform
	 * @param locale
	 * @throws Exception
	 */
	@Parameters({"platform"})
	@BeforeClass (alwaysRun = true)
	public void setUp(String platform) throws Exception{
		/**
		 * this sets ThreadContext map with key THREADCONTEXT_ROUTINGKEY and value from browser param.
	    this is needed despite setting ThreadContext in beforeTest and beforeMethod. When classes are run 
	    parallel, thread pool executing classes in parallel does not inherit ThreadContext set in beforeTest 
	    at test initialization. Log messages coming from other beforeClass methods such as login and 
	    feature flip needs to separated based on browser as well. 
		 */
	    ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
	    if (platform.equalsIgnoreCase("android")) {
			driver = DriverFactory.getAndroidInstance();
			logger.info("WEBDRIVER IS STARTING ON REMOTE SESSION");
		} else if (platform.equalsIgnoreCase("ios")) {
			driver      = DriverFactory.getIosInstance();
			logger.info("WEBDRIVER IS STARTING ON LOCAL SESSION");
		}
	    
	    PlatformController controller = new PlatformController(driver);
	    app = controller.getPlatform(platform);
	}
	
	@BeforeSuite
	public void beforeSuiteSetUp() {
        extentReports = new ExtentReports(System.getProperty("user.dir") +"/test-output/extent-report.html");
    }
	
	@BeforeClass
    public void beforeClassSetUp(ITestContext context) {
	    extentTest = extentReports.startTest(context.getCurrentXmlTest().getClasses().stream().map(e -> e.getName()).collect(Collectors.toList()).get(executedClassCount));
	    executedClassCount++;
    }
	
	@AfterSuite
	public void afterSuiteSetUp() {
        extentReports.endTest(extentTest);
        extentReports.flush();
    }

    /**
	 * Closes the current Session
	 */
	protected void quitSession() {
		driver.close();
	}
	
	/**
	 * Quit the driver, once the tests are completed
	 */
	@AfterClass	(alwaysRun = true)
	public void tearDown() {
		try {
			if (driver != null) 
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    ThreadContext.clearMap();
        }
		
	}
	
    /**
     * This is run before any xml test is run and set a thread context with key THREADCONTEXT_ROUTINGKEY
     * and value is from browser param. This is needed despite setting ThreadContext in beforeClass 
     * and beforeMethod to separate logs based on browser happening before test class initialization 
     * For e.g. some logs in TestListener and ConfigReader are run before test class initialized but we know
     * already that test class will be initialized for execution in specific browser depending on params in 
     * xml file or jvm args
     * 
     * @param browser
     */
    @BeforeTest(alwaysRun = true)
    @Parameters({ "platform" })
    public void beforeTestThreadContextSetup(String platform) {
        ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
    }

    /**
     * This clears the ThreadContextMap
     */
    @AfterTest(alwaysRun = true)
    public void afterTestThreadContextCleanup() {
        ThreadContext.clearMap();
    }
    
    /**
     * This is run before any test method is run and set a thread context with key THREADCONTEXT_ROUTINGKEY
     * and value is from browser param. This is needed despite of setting ThreadContext in beforeClass and
     * beforeTest. When methods are run parallel (instead of class or tests) then the threadpool executing 
     * methods in parallel does not have/inherit the context map of parent thread. Hence each method
     * 
     * @param browser
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({ "platform" })
    public void beforeMethodThreadContextSetup(String platform) {
        ThreadContext.put(THREADCONTEXT_ROUTINGKEY, platform.toLowerCase());
    }

    public static AppiumDriver getDriver() {
        return driver;
    }
}
