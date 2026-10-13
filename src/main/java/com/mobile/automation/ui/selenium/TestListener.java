package com.mobile.automation.ui.selenium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.mobile.automation.core.RetryFailedTests;
import com.mobile.automation.util.Util;
import com.relevantcodes.extentreports.LogStatus;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.DisabledRetryAnalyzer;
import org.testng.xml.XmlSuite;

public class TestListener extends TestListenerAdapter implements IAnnotationTransformer, IReporter {
    public Logger logger = LogManager.getLogger();
    private static final String LOG_4J_Path = "/resources/config/log4j2.xml";
    private static final String emailableReportTemplateFile = System.getProperty("user.dir") + "/resources/template/report_template.html";
    private static String emailableReport = null;
    private static String startTime = getDateInStringFormat(new Date());
    Map<String, String[]> suiteAnalysis = new HashMap<String, String[]>();
    Map<String, List<String[]>> testAnalysis = new HashMap<String, List<String[]>>();

    public void onTestStart(ITestResult result) {
        logger.info("############## STARTING TEST " + result.getName() + " ####################");
    }

    static {
        System.setProperty("log4j.configurationFile", System.getProperty("user.dir") + LOG_4J_Path);
        Util.deleteAllFilesFromFolder(System.getProperty("user.dir") + "/resources/screenshots/");
    }
    
    private void createSuiteExecutionAnalysisSection(List<ISuite> suites) {
        StringBuffer retBuf = new StringBuffer();
        
        for (ISuite tempSuite : suites) {
            retBuf.append("<table style=\"" + "margin-left:60px; width: 95%;"
                    + "\"> <thead> <tr> <th>Test Name</th> <th># Total Method</th> <th># Passed</th> <th># Skipped</th> <th># Failed</th> <th>Browser</th> <th>Execute Time (mm:ss:ms)</th> </tr> </thead> <tr> <td colspan=11> <center><b>"
                    + tempSuite.getName() + "</b></center> </td> </tr>");
            Map<String, ISuiteResult> testResults = tempSuite.getResults();
            int testCount = 0;
            
            for (ISuiteResult result : testResults.values()) {

                ITestContext testObj = result.getTestContext();
                ITestNGMethod[] iTestNGMethod = testObj.getAllTestMethods();
                
                int totalTestPassed = 0;
                int totalTestFailed = 0;
                int totalTestSkipped = 0;
                long executionTime = 0;
                
                for (int i = 0; i < iTestNGMethod.length; i++) {
                    
                    String[] suiteExecutionDetails = new String[5];

                    IResultMap failedTestMap = testObj.getFailedTests();
                    IResultMap passedTestMap = testObj.getPassedTests();
                    IResultMap skippedTestMap = testObj.getSkippedTests();
                    
                    if(suiteAnalysis.containsKey(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName())) {
                        suiteExecutionDetails = suiteAnalysis.get(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName());
                        totalTestPassed = Integer.parseInt(suiteExecutionDetails[0]);
                        totalTestFailed = Integer.parseInt(suiteExecutionDetails[1]);
                        totalTestSkipped = Integer.parseInt(suiteExecutionDetails[2]);
                        executionTime = Long.parseLong(suiteExecutionDetails[3]);
                    } else {
                        totalTestPassed = 0;
                        totalTestFailed = 0;
                        totalTestSkipped = 0;
                        executionTime = 0;
                    }
                    
                    for (ITestResult testResult : failedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            totalTestFailed = totalTestFailed + 1;
                            executionTime = executionTime + (testResult.getEndMillis() - testResult.getStartMillis());
                            break;
                        }
                    }
                    for (ITestResult testResult : passedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            totalTestPassed = totalTestPassed + 1;
                            executionTime = executionTime + (testResult.getEndMillis() - testResult.getStartMillis());
                            break;
                        }
                    }
                    for (ITestResult testResult : skippedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            totalTestSkipped = totalTestSkipped + 1;
                            executionTime = executionTime + (testResult.getEndMillis() - testResult.getStartMillis());
                            break;
                        }
                    }
                    
                    suiteExecutionDetails[0] = String.valueOf(totalTestPassed);
                    suiteExecutionDetails[1] = String.valueOf(totalTestFailed);
                    suiteExecutionDetails[2] = String.valueOf(totalTestSkipped);
                    suiteExecutionDetails[3] = String.valueOf(executionTime);
                    suiteExecutionDetails[4] = tempSuite.getXmlSuite().getTests().get(testCount).getParameter("browser");
                    suiteAnalysis.put(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName(), suiteExecutionDetails);
                    
                }
                testCount++;
            }
            retBuf.append(addTestResultsInTable());
            retBuf.append("</table>");
        }
        emailableReport = emailableReport.replaceAll("\\$SUITE-ANALYSIS\\$", retBuf.toString());
    }
    
    private String addTestResultsInTable() {
        StringBuffer retBuf = new StringBuffer();
        for (String className : suiteAnalysis.keySet()) {
            
            retBuf.append("<tr>");
            
            /* Test name. */
            retBuf.append("<td>");
            retBuf.append(className);
            retBuf.append("</td>");
    
            /* Total method count. */
            retBuf.append("<td>");
            retBuf.append(Integer.parseInt(suiteAnalysis.get(className)[0]) + Integer.parseInt(suiteAnalysis.get(className)[1]) + Integer.parseInt(suiteAnalysis.get(className)[2]));
            retBuf.append("</td>");
    
            /* Passed method count. */
            retBuf.append("<td bgcolor=lightgreen>");
            retBuf.append(suiteAnalysis.get(className)[0]);
            retBuf.append("</td>");
    
            /* Skipped method count. */
            retBuf.append("<td bgcolor=#ff4e3dn>");
            retBuf.append(suiteAnalysis.get(className)[2]);
            retBuf.append("</td>");
    
            /* Failed method count. */
            retBuf.append("<td bgcolor=red>");
            retBuf.append(suiteAnalysis.get(className)[1]);
            retBuf.append("</td>");
    
            /* Append browser type. */
            retBuf.append("<td>");
            retBuf.append(suiteAnalysis.get(className)[4]);
            retBuf.append("</td>");
    
            /* Execute Time */
            retBuf.append("<td>");
            long minutes = (Long.parseLong(suiteAnalysis.get(className)[3]) / 1000) / 60;
            long seconds = (Long.parseLong(suiteAnalysis.get(className)[3]) / 1000) % 60;
            retBuf.append(minutes + "m " + seconds + "s");
            retBuf.append("</td>");
    
            retBuf.append("</tr>");
            
        }
        
        return retBuf.toString();
    }
    
    private void createTestAnalysisSection(List<ISuite> suites) {
        StringBuffer retBuf = new StringBuffer();
        retBuf.append(addOverallTestResults());
        emailableReport = emailableReport.replaceAll("\\$TEST_ANALYSIS\\$", retBuf.toString());
    }
    
    private String addOverallTestResults() {
        StringBuffer retBuf = new StringBuffer();
        int classIndex = 0;
        
        for (String className : suiteAnalysis.keySet()) {
            
            if(classIndex == 0) {
                retBuf.append("<a class=\"nav-link active\" id=\"v-pills-home-tab\" onclick=\"showHideResults(" + classIndex + ")\" data-toggle=\"pill\" href=\"#v-pills-home\" role=\"tab\" aria-controls=\"v-pills-home\" aria-selected=\"true\">");
            } else {
                retBuf.append("<a class=\"nav-link\" id=\"v-pills-home-tab\" onclick=\"showHideResults(" + classIndex + ")\" data-toggle=\"pill\" href=\"#v-pills-home\" role=\"tab\" aria-controls=\"v-pills-home\" aria-selected=\"true\">");
            }
            
            retBuf.append("<div class='test-head'>");
            retBuf.append("<span class='test-name'>" + className + "</span>");
            
            if(Integer.parseInt(suiteAnalysis.get(className)[1]) == 0) {
                retBuf.append("<span class='test-status label right outline capitalize pass'>pass</span>");
            } else {
                retBuf.append("<span class='test-status label right outline capitalize fail'>fail</span>");
            }
            
            retBuf.append("<span class='category-assigned hide '></span>");
            retBuf.append("</div>");
            retBuf.append("</a>");
            
            classIndex++;
            
        }
        
        return retBuf.toString();
    }
    
    private void createMethodAnalysisSection(List<ISuite> suites) {
        StringBuffer retBuf = new StringBuffer();
        
        for (ISuite tempSuite : suites) {
            Map<String, ISuiteResult> testResults = tempSuite.getResults();

            for (ISuiteResult result : testResults.values()) {
                
                List<String[]> classAnalysis = new ArrayList<String[]>();

                ITestContext testObj = result.getTestContext();
                ITestNGMethod[] iTestNGMethod = testObj.getAllTestMethods();
                
                for (int i = 0; i < iTestNGMethod.length; i++) {
                    
                    String[] methodAnalysis =  new String[4];
                    long executionTime = 0;
                    
                    IResultMap failedTestMap = testObj.getFailedTests();
                    IResultMap passedTestMap = testObj.getPassedTests();
                    IResultMap skippedTestMap = testObj.getSkippedTests();
                    
                    if(testAnalysis.containsKey(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName())) {
                        classAnalysis = testAnalysis.get(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName());
                    } else {
                        classAnalysis = new ArrayList<String[]>();
                    }
                    
                    for (ITestResult testResult : failedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            methodAnalysis[0] = "fail"; 
                            executionTime = testResult.getEndMillis() - testResult.getStartMillis();
                            methodAnalysis[1] = String.valueOf(executionTime);
                            methodAnalysis[2] = iTestNGMethod[i].getMethodName();
                                    
                            Throwable exception = testResult.getThrowable(); 
                            StringWriter stringWriter = new StringWriter();
                            PrintWriter printWriter = new PrintWriter(stringWriter);
                            exception.printStackTrace(printWriter);
                            
                            methodAnalysis[3] =  stringWriter.toString();
                            break;
                        }
                    }
                    for (ITestResult testResult : passedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            methodAnalysis[0] = "pass"; 
                            executionTime = testResult.getEndMillis() - testResult.getStartMillis();
                            methodAnalysis[1] = String.valueOf(executionTime);
                            methodAnalysis[2] = iTestNGMethod[i].getMethodName();
                            break;
                        }
                    }
                    for (ITestResult testResult : skippedTestMap.getAllResults()) {
                        if (iTestNGMethod[i].getMethodName() == testResult.getMethod().getMethodName()) {
                            methodAnalysis[0] = "skip"; 
                            executionTime = testResult.getEndMillis() - testResult.getStartMillis();
                            methodAnalysis[1] = String.valueOf(executionTime);
                            methodAnalysis[2] = iTestNGMethod[i].getMethodName();
                            break;
                        }
                    }
                    
                    if(methodAnalysis[0] != null) {
                        classAnalysis.add(methodAnalysis);
                        testAnalysis.put(iTestNGMethod[i].getTestClass().getRealClass().getSimpleName(), classAnalysis);
                    }
                }
                
            }
            retBuf.append(addMethodResultsInTable());
        }
        emailableReport = emailableReport.replaceAll("METHOD_ANALYSIS", retBuf.toString());
    }
    
    private String addMethodResultsInTable() {
        StringBuffer retBuf = new StringBuffer();
        int classIndex = 0;
        
        for (String className : suiteAnalysis.keySet()) {
            
            if(classIndex == 0) {
                retBuf.append("<div class=\"tab-pane fade show active\" role=\"tabpanel\" aria-labelledby=\"v-pills-profile-tab\">");
                retBuf.append("<div class=\"test-steps\">");
                retBuf.append("<table class='bordered table-results'>");
                retBuf.append("<thead>");
                retBuf.append("<tr>");
                retBuf.append("<th>Status</th>");
                retBuf.append("<th>Timestamp</th>");
                retBuf.append("<th>Details</th>");
                retBuf.append("</tr>");
                retBuf.append("</thead>");
                retBuf.append("<tbody>");
            } else {
                retBuf.append("<div class=\"tab-pane fade\" role=\"tabpanel\" aria-labelledby=\"v-pills-profile-tab\">");
                retBuf.append("<div class=\"test-steps\">");
                retBuf.append("<table class='bordered table-results'>");
                retBuf.append("<thead>");
                retBuf.append("<tr>");
                retBuf.append("<th>Status</th>");
                retBuf.append("<th>Execution Time</th>");
                retBuf.append("<th>Details</th>");
                retBuf.append("</tr>");
                retBuf.append("</thead>");
                retBuf.append("<tbody>");
            }
            
            List<String[]> classAnalysis = testAnalysis.get(className);
            for (String[] methodDetails : classAnalysis) {
                
                long minutes = (Long.parseLong(methodDetails[1]) / 1000) / 60;
                long seconds = (Long.parseLong(methodDetails[1]) / 1000) % 60;
                
                retBuf.append("<tr>");
                switch (methodDetails[0].toLowerCase()) {
                case "pass":
                    retBuf.append("<td class='status pass' title='pass' alt='pass'><i class='mdi-action-check-circle'></i></td>");
                    break;
                case "fail":
                    retBuf.append("<td class='status fail' title='fail' alt='fail'><i class='mdi-navigation-cancel'></i></td>");
                    break;
                case "skip":
                    retBuf.append("<td class='status skip' title='skip' alt='skip'><i class='mdi-content-redo'></i></td>");
                    break;
                default:
                    break;
                }
                
                retBuf.append("<td class='timestamp'>" + minutes + "m " + seconds + "s" + "</td>");
                
                switch (methodDetails[0].toLowerCase()) {
                case "pass":
                    retBuf.append("<td class='step-details'>" + methodDetails[2] + "</td>");
                    break;
                case "fail":
                    retBuf.append("<td class='step-name'>" + methodDetails[2]);
                    retBuf.append("<div class='w3-row-padding'>");
                    retBuf.append("<div class='w3-container w3-third'>");
                    retBuf.append("<img src='" + System.getProperty("user.dir") + "/resources/screenshots/" + methodDetails[2] + ".png' style='width: 200px; height: 100px; cursor: pointer' onclick='onClick(this)' class='w3-hover-opacity'>");
                    retBuf.append("</div>");
                    retBuf.append("</div>");
                    retBuf.append("</td>");
                    retBuf.append("<td class='step-details'> <pre>" + methodDetails[3] + "</pre> </td>");
                    break;
                case "skip":
                    retBuf.append("<td class='step-details'>" + methodDetails[2] + "</td>");
                    break;
                default:
                    break;
                }
                
                retBuf.append("</tr>");
            }
            
            retBuf.append("</tbody>");
            retBuf.append("</table>");
            retBuf.append("<ul class=\"collapsible node-list\" data-collapsible=\"accordion\">");
            retBuf.append("</ul>");
            retBuf.append("</div>");
            retBuf.append("</div>");
        
            classIndex++;
        }
        
        return retBuf.toString();
    }
    
    private void createPieChartSection(List<ISuite> suites) {
        int totalTestCount = 0;
        int totalTestPassed = 0;
        int totalTestFailed = 0;
        int totalTestSkipped = 0;

        for (ISuite tempSuite : suites) {
            Map<String, ISuiteResult> testResults = tempSuite.getResults();
            for (ISuiteResult result : testResults.values()) {
                ITestContext testObj = result.getTestContext();

                totalTestPassed = testObj.getPassedTests().getAllMethods().size();
                totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
                totalTestFailed = testObj.getFailedTests().getAllMethods().size();

                totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;
            }
        }
        emailableReport = emailableReport.replaceAll("\\$TOTAL_TEST\\$", String.valueOf(totalTestCount));
        emailableReport = emailableReport.replaceAll("\\$TOTAL_PASSED\\$", String.valueOf(totalTestPassed));
        emailableReport = emailableReport.replaceAll("\\$TOTAL_SKIPPED\\$", String.valueOf(totalTestSkipped));
        emailableReport = emailableReport.replaceAll("\\$TOTAL_FAILED\\$", String.valueOf(totalTestFailed));
    }
    
    private void createDashboardSection(List<ISuite> suites) throws UnknownHostException {
        emailableReport = emailableReport.replaceAll("\\$SUITE_START_TIME\\$", startTime);
        long deltaTime = new Date().getTime() - new Date(startTime).getTime();
        emailableReport = emailableReport.replaceAll("\\$EXECUTION_TIME\\$", this.convertDeltaTimeToString(deltaTime));
        emailableReport = emailableReport.replaceAll("\\$START_TIME\\$", startTime);
        emailableReport = emailableReport.replaceAll("\\$END_TIME\\$", TestListener.getDateInStringFormat(new Date()));
        emailableReport = emailableReport.replaceAll("\\$USERNAME\\$", System.getProperty("user.name"));
        emailableReport = emailableReport.replaceAll("\\$OS\\$", System.getProperty("os.name"));
        emailableReport = emailableReport.replaceAll("\\$JAVA-VERSION\\$", System.getProperty("java.version"));
        emailableReport = emailableReport.replaceAll("\\$HOST-NAME\\$", InetAddress.getLocalHost().getHostName());
    }
    
    private void readEmailabelReportTemplate() {
        StringBuffer retBuf = new StringBuffer();
        FileReader fr;
        BufferedReader br;
        try {
            File file = new File(emailableReportTemplateFile);
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                retBuf.append(line);
                line = br.readLine();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            emailableReport = retBuf.toString();
        }
    }
    
    /* Get date string format value. */
    private static String getDateInStringFormat(Date date) {
        StringBuffer retBuf = new StringBuffer();
        if (date == null) {
            date = new Date();
        }
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        retBuf.append(df.format(date));
        return retBuf.toString();
    }

    /* Convert long type deltaTime to string with format hh:mm:ss. */
    private String convertDeltaTimeToString(long deltaTime) {
        StringBuffer retBuf = new StringBuffer();
        long minutes = (deltaTime / 1000) / 60;
        long seconds = (deltaTime / 1000) % 60;
        long hours = minutes / 60;
        retBuf.append(hours + "h " + minutes + "m " + seconds + "s ");
        return retBuf.toString();
    }

    /**
     * Update the test case browser result to PASS. Updates test case final
     * result to FAIL if any browser has status fail If no browser has FAIL
     * status then mark final status as PASS. Serialize suiteResult to JSON
     * 
     * @param result
     */
    public void onTestSuccess(ITestResult result) {
        logger.info("------------------- " + result.getName() + " : PASSED -------------------");
        removeSkippedTest(result);
        BaseTestSuite.extentTest.log(LogStatus.PASS, result.getName() + " : PASSED");
    }

    public void onTestFailure(ITestResult result) {
        logger.info("------------------- " + result.getName() + " : FAILED -------------------");
        removeSkippedTest(result);
        BaseTestSuite.extentTest.log(LogStatus.FAIL, result.getName() + " : FAILED", result.getThrowable());
        takeScreenshot(result);
    }

    public void onTestSkipped(ITestResult result) {
        logger.info("------------------- " + result.getName() + " : SKIPPED -------------------");
        BaseTestSuite.extentTest.log(LogStatus.SKIP, result.getName() + " : SKIPPED", result.getThrowable());
    }
    
    public void onFinish(ITestContext context) {
        logger.info("####### FINISHED EXECUTING SUITE ########");
    }
    
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            readEmailabelReportTemplate();
            createPieChartSection(suites);
            createDashboardSection(suites);
            createSuiteExecutionAnalysisSection(suites);
            createTestAnalysisSection(suites);
            createMethodAnalysisSection(suites);
            File targetFile = new File(outputDirectory + "/custom-emailable-report.html");
            FileWriter fw = new FileWriter(targetFile);
            fw.write(emailableReport);
            fw.flush();
            fw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Run Failed Test cases again
     */
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Class<? extends IRetryAnalyzer> retry = annotation.getRetryAnalyzerClass();
        if (retry == null || retry == DisabledRetryAnalyzer.class) {
            annotation.setRetryAnalyzer(RetryFailedTests.class);
        }
    }

    /**
     * When a test case fails and retried, the it is marked as SKIP. SKIP test
     * case also appears in failed-testng.xml and jenkins retries it regardless
     * of result in retry attempt This function clears all such SKIP entries.
     * 
     * @param iResult
     */
    public void removeSkippedTest(ITestResult iResult) {
        ITestContext context = iResult.getTestContext();
        Iterator<ITestResult> skippedTC = context.getSkippedTests().getAllResults().iterator();
        while (skippedTC.hasNext()) {
            ITestResult skipped = skippedTC.next();
            ITestNGMethod skippedMethod = skipped.getMethod();
            if (context.getSkippedTests().getResults(skippedMethod).size() > 0) {
                logger.info("Removing skipped entry for test case: " + skipped.getName());
                skippedTC.remove();
            }
        }
    }
    
    public void takeScreenshot(ITestResult result) {
        try {
            Screenshot myScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(BaseTestSuite.getDriver());
            ImageIO.write(myScreenshot.getImage(),"PNG", new File(System.getProperty("user.dir") + "/resources/screenshots/" + result.getName() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
