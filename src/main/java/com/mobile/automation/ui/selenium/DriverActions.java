package com.mobile.automation.ui.selenium;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mobile.automation.util.Util;

import io.appium.java_client.AppiumDriver;

/**
 * Wrappers for element actions: click, type, read text, waits, gestures by Actions.
 * Every page object inherits them through BasePage, so pages and tests never call
 * the driver directly and every action is waited for and logged in one place.
 *
 * Locator strings (for the String overloads) are resolved by {@link #getByFromLocator(String)}:
 * "//..." is an XPath, "class=", "css=", "name=", "link=", "tag=" pick that strategy,
 * anything else is an element id.
 *
 * Methods marked "Web/hybrid context only" use JavaScript, windows, frames, page
 * title/URL or HTML selects. A native Android/iOS screen has none of these, so they
 * only work inside a web view (after switching to its WEBVIEW context) or in a
 * mobile browser. They come from the web framework this one started from.
 *
 * @author rmathews
 *
 */

public abstract class DriverActions{

	protected WebDriver driver;
	// Shared wait: up to DEFAULT_WAIT_TIME seconds, checking every POOLING_WAIT_TIME seconds
	protected Wait<WebDriver> webDriverWait;
	public Logger logger = LogManager.getLogger();
	// Window handles remembered by switchToNewTab() for switchToParentTab()
	private ArrayList<String> tabs;
	/** Default wait for elements, in seconds. */
	public static final long DEFAULT_WAIT_TIME = 20;
	/** Polling interval of the shared wait, in seconds. */
	public static final long POOLING_WAIT_TIME = 2;
	protected Actions actions;

	/**
	 * @param webDriver the running session
	 */
	public DriverActions(AppiumDriver webDriver) {
		this.driver = webDriver;
		actions = new Actions(webDriver);
		this.webDriverWait = new FluentWait<WebDriver>(driver)
	            .withTimeout(Duration.ofSeconds(DEFAULT_WAIT_TIME))
	            .pollingEvery(Duration.ofSeconds(POOLING_WAIT_TIME))
	            .ignoring(NoSuchElementException.class);
	}

	/**
	 * @return WebDriver the session this page works on
	 */
	public WebDriver getWebDriver() {
		return driver;
	}

	/**
	 * Open the given Url
	 * Web/hybrid context only.
	 *
	 * @param url
	 */
	public void openUrl(String url) {
		driver.get(url);
		Util.sleepInSec(Util.M_WAIT, "Wait for page to load");
		logger.info("Opened the url :"+ url);
	}

	/**
	 * Refresh the current browser session
	 * Web/hybrid context only.
	 *
	 */
	public void refresh() {
		driver.navigate().refresh();
		Util.sleepInSec(Util.M_WAIT, "Wait for page load");
		logger.info("Refreshed the page");
	}

	/**
	 * Returns the Current Page's Title
	 * Web/hybrid context only.
	 *
	 * @return browserTitle
	 */
	public String getBrowserTitle() {
		String title = driver.getTitle();
		logger.info("The browser title is " + title);
		return title;
	}

	/**
	 * Returns the Current Page's URL
	 * Web/hybrid context only.
	 *
	 * @param browser not used; kept for existing callers
	 * @return browserURL
	 */
	public String getBrowserURL(String browser) {
		String url = null;
		url = driver.getCurrentUrl();
		logger.info("Browser URL is " + url);
		return url;
	}

	/**
	 * Get the Browser Name from the session capabilities
	 *
	 * @return browserName - example chrome; empty for a native app session
	 */
	public String getBrowser() {
		Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
		return cap.getBrowserName();
	}

	/**
	 * Waits until the element is visible, then clicks it (one retry, see below).
	 *
	 * @param element - WebElement to click
	 * @return true when the click went through
	 */

	public boolean clickElement(WebElement element) {
		waitForElementPresent(element, DEFAULT_WAIT_TIME);
	    return clickElement(element, true);
	}

	/**
	 * Internal click with one retry.
	 *
	 * When the screen redraws between finding and clicking, the element is gone or
	 * not yet interactable. Page-object fields are proxies that look the element up
	 * again on every call, so a second try on the same element reaches the new one.
	 * (The old version cut an "xpath:" out of the element's text, which failed for
	 * any other locator, such as the Appium ids used here.)
	 *
	 * @param element the element to click
	 * @param retry   true to allow one more try; the retry passes false, so it never loops
	 * @return true when the click went through
	 */
	private boolean clickElement(WebElement element, boolean retry) {
		boolean result = false;
		if (element != null) {
			try {
				element.click();
				logger.info("Clicked On the element: " + element);
				result = true;
			} catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException e) {
				if (retry) {
				    // Give the screen a moment to settle, then try once more
				    Util.sleepInSec(Util.S_WAIT);
				    result = clickElement(element, false);
				} else {
					logger.error("Cannot click on element :" + element);
				    logger.info("Stacktrace: \n" + e);
				}
				return result;
			} catch (Exception e) {
				logger.error("Cannot click on element :" + element);
				logger.info("Stacktrace: \n", e);
				return false;
			}
		}
		return result;
	}

	/**
	 * Click an Element with its locator, waiting up to DEFAULT_WAIT_TIME
	 *
	 * @param locator see {@link #getByFromLocator(String)}
	 * @return true/false based on if click was successful or not
	 */
	public boolean clickElement(String locator) {
		return clickElement(locator, DEFAULT_WAIT_TIME);
	}

	/**
	 * Click an Element with its locator and maxWaitTime
	 *
	 * @param locator     see {@link #getByFromLocator(String)}
	 * @param maxWaitTime seconds to wait for the element to be present
	 * @return true/false based on if click was successful or not
	 */
	public boolean clickElement(String locator, long maxWaitTime) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWaitTime));
	    WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(getByFromLocator(locator)));
	    return clickElement(element);
	}

	/**
	 * Function to execute a JavaScript
	 * Web/hybrid context only.
	 *
	 * @param script
	 * @return long
	 */
	public int executeJavaScript(String script) {
		long itemNum = -1;
		try {
			itemNum = (Long) ((JavascriptExecutor) driver).executeScript("{" + script + "}");
			logger.info("Executed the Javascript successfully");
		} catch (Throwable t) {
			logger.error("Exception occurred while executing JavaScript, returning itemNum=-1");
			return (int) itemNum;
		}
		return (int) itemNum;
	}

	/**
	 * Wrapper function to execute a JavaScript
	 * Web/hybrid context only.
	 *
	 * @param script
	 * @return WebElement
	 */
	public WebElement executeJavaScriptForWebElement(String script) {
		WebElement element = null;
		try {
			element = (WebElement) ((JavascriptExecutor) driver).executeScript("{" + script + "}");
			logger.info("Executed the Javascript successfully");
		} catch (Throwable t) {
			logger.info("Exception occurred on executing JavaScript, returning WebElement = NULL");
			return element;
		}
		return element;
	}

	/**
	 * Click on a WebElement using JavaScriptExecutor
	 * Web/hybrid context only.
	 *
	 * @param WebElement
	 */
	public void clickJavaScript(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
		logger.info("Clicked on the WebElement" +element);
	}

	/**
	 * Verify if checkbox is Checked or not, using JavaScript
	 * Web/hybrid context only.
	 *
	 * @param WebElement
	 * @return true/false
	 */
	public Boolean isCheckBoxChecked(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		Boolean checkBox = (Boolean) executor.executeScript("return arguments[0].checked", element);
		return checkBox;
	}

	/**
	 * Right Click a WebElement and select the option
	 *
	 * @param WebElement
	 * @param option     to click as String
	 */
	public void selectItemRightClick(WebElement element, String optionToClick) {
		Actions action = new Actions(driver);
		action.contextClick(element).build().perform();
		WebElement webElementToClick = getElement(optionToClick);
		clickElement(webElementToClick);
	}

	/**
	 * Right Click a WebElement
	 *
	 * @param element
	 * @param Item
	 */
	public void rightClick(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element);
		action.contextClick(element).build().perform();
		logger.info("Right clicked on the WebElement "+element);
	}

	/**
	 * sendKeys
	 *
	 * @param key - example KEYS.CONTROL, KEYS.DELETE
	 */
	public void keyTouch(Keys key) {
		Actions action = new Actions(driver);
		action.sendKeys(key);
		action.perform();
	}

	/**
	 * Press key multiple times
	 *
	 * @param key   - example KEYS.CONTROL, KEYS.DELETE
	 * @param count - count of how many times you want the key to be pressed
	 */
	public void keyTouch(Keys key, int count) {
		for (int i = 0; i < count; i++) {
			keyTouch(key);
		}
	}

	/**
	 * Wrapper function to enter text in a webElement (aka sendKeys), clearing existing data if needed.
	 * Waits until the element is visible; retries once if the screen redrew in between.
	 *
	 * @param element           - WebElement to type into
	 * @param data              - text to type
	 * @param clearExistingData - true to empty the field first
	 */
	public void sendKeys(WebElement element, String data, Boolean clearExistingData) {
		sendKeys(element, data, clearExistingData, true);
	}

	/**
	 * Internal sendKeys with one retry (same reasoning as the click retry).
	 *
	 * @param retry true to allow one more try; the retry passes false
	 */
	private void sendKeys(WebElement element, String data, Boolean clearExistingData, boolean retry) {
	    Util.sleepInSec(Util.S_WAIT);
		this.webDriverWait.until(ExpectedConditions.visibilityOf(element));
		try {
			if (clearExistingData) {
				element.clear();
			}
			element.sendKeys(data);
			logger.info("Send Keys On Element :: " + element + " With Data : " + data);
		} catch (StaleElementReferenceException e) {
			if (retry) {
				sendKeys(element, data, clearExistingData, false);
			} else {
				logger.error("Cannot enter data on element :" + element);
			    logger.info("Stacktrace: \n" + e);
			}
		} catch (Exception e) {
			logger.error("Cannot enter data on element :" + element);
			logger.info("Stacktrace: \n", e);
		}
	}

	/**
	 * Sendkeys (or enter text) Wrapper using locator, clearing existing data if needed
	 *
	 * @param locator           see {@link #getByFromLocator(String)}
	 * @param data              text to type
	 * @param clearExistingData true to empty the field first
	 */
	public void sendKeys(String locator, String data, Boolean clearExistingData) {
		WebElement element = this.getElement(locator);
		sendKeys(element, data, clearExistingData);
	}

	/**
	 * Sendkeys (or enter text) Wrapper using WebElement, keeping existing text
	 *
	 * @param element the field
	 * @param data    text to type
	 */
	public void sendKeys(WebElement element, String data) {
		this.sendKeys(element, data, false);
	}

	/**
	 * Sendkeys (or enter text) Wrapper using locator, keeping existing text
	 *
	 * @param locator see {@link #getByFromLocator(String)}
	 * @param data    text to type
	 */
	public void sendKeys(String locator, String data) {
		WebElement element = this.getElement(locator);
		this.sendKeys(element, data, false);
	}

	/**
	 * Send keys with element
	 *
	 * @param element
	 * @param key     - example : Keys.BACK_SPACE, KEYS.CONTROL
	 */
	public void sendKeys(WebElement element, Keys key) {
		element.sendKeys(key);
	}

	/**
	 * Send keys with locator
	 *
	 * @param locator
	 * @param key     - example : Keys.BACK_SPACE, KEYS.CONTROL
	 */
	public void sendKeys(String locator, Keys key) {
		WebElement element = this.getElement(locator);
		element.sendKeys(key);
	}

	/**
	 * Scrolls such that element is in the view.
	 * Web/hybrid context only.
	 *
	 * @param WebElement
	 * @param isElementAlignedToTopOfView If isElementAlignedToTopOfView is true,
	 *                                    method tries to align the element to the
	 *                                    top of the view If
	 *                                    isElementAlignedToTopOfView is false,
	 *                                    method tries to align the element to the
	 *                                    to bottom of view.
	 */

	public void scrollToElement(WebElement element, boolean isElementAlignedToTopOfView) {
		String js = "arguments[0].scrollIntoView(" + isElementAlignedToTopOfView + ");";
		executeScript(js, element);
		logger.info("Scrolled to Element "+element);
	}

	/**
	 * Scroll to the WebElement
	 *
	 * @param element
	 */

	public void scrollToElement(WebElement element) {
		scrollToElement(element, true);
		logger.info("Scrolled to Element "+element);
	}

	/**
	 * Scroll down
	 *
	 * @param element
	 */
	public void scrollDown(WebElement element) {
		executeScript("arguments[0].lastChild.scrollIntoView(true);", element);
	}

	/**
	 * Execute javascript
	 * Web/hybrid context only.
	 *
	 * @param script
	 * @param args
	 */
	public void executeScript(String script, Object... args) {
		((JavascriptExecutor) driver).executeScript(script, args);
	}

	/**
	 * Scrolling element by pixels along x axis.
	 * 		Positive x means right,
	 * 		Negative x means left
	 * 		Positive y means down
	 * 		Negative y means up
	 *
	 * @param element
	 * @param x_axis
	 * @param y_axis
	 */
	public void scrollElementByPixels(WebElement element, int x_axis, int y_axis) {
		String jString = String.format("arguments[0].scrollBy(%d, %d)", x_axis, y_axis);
		executeScript(jString, element);
	}

	/**
	 *
	 * Gets the text from the WebElement passed
	 *
	 * @param element the element to read
	 * @return the text without surrounding spaces, or null when the element is null
	 */
	public String getText(WebElement element) {
		String text = null;
		if (element != null) {
			text = element.getText();
			if (text.length() != 0)
				logger.info("Getting Text On Element :: " + element + " The Text is : " + text);
		} else {
			logger.error("getText called with a null element");
			return null;
		}
		return text.trim();
	}

	/**
	 *
	 * Gets the text from the locator passed
	 *
	 * @param locator see {@link #getByFromLocator(String)}
	 * @return the text without surrounding spaces
	 */
	public String getText(String locator) {
		WebElement element = this.getElement(locator);
		return this.getText(element);
	}

	/**
	 * Returns the state of the element, if the element is Enabled or not, using locator
	 *
	 * @param locator
	 * @return true or false
	 */

	public boolean isEnabled(String locator) {
		return (getElement(locator)).isEnabled();
	}

	/**
	 * Returns the state of the element, if the element is Enabled or not, using WebElement
	 *
	 * @param locator
	 * @return
	 */

	public Boolean isEnabled(WebElement element) {
		return element.isEnabled();
	}

	/**
	 * Returns the state of the element, if the element is displayed or not, based on WebElement
	 *
	 * @param element
	 * @return
	 */

	public boolean isDisplayed(WebElement element) {
		return (element).isDisplayed();
	}

	/**
	 * Returns the state of the element, if the element is displayed or not, based on locator
	 *
	 * @param locator
	 * @return
	 */
	public boolean isDisplayed(String locator) {
		return (getElement(locator)).isDisplayed();
	}

	/**
	 * Returns the state of the element, if the element is selected or not, using Locator
	 *
	 * @param locator
	 * @return boolean
	 */
	public boolean isSelected(String locator) {
		return (getElement(locator).isSelected());
	}

	/**
	 * Returns the state of the element, if the element is selected or not, using WebElement
	 *
	 * @param locator
	 * @return boolean
	 */

	public boolean isSelected(WebElement element) {
		return (element.isSelected());
	}

	/**
	 * select a checkbox based on webElement
	 *
	 * @param element
	 */
	public void check(WebElement element) {
		this.webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
		if (element.isSelected()) {
			logger.info("The checkbox " + element + " is already checked");
		} else {
			element.click();
			logger.info("The checkbox " + element + " is checked");
		}
	}

	/**
	 * select a checkbox based on locator
	 *
	 * @param locator
	 */
	public void check(String locator) {
		WebElement element = getElement(locator);
		check(element);
	}

	/**
	 * Unselect a checkbox
	 *
	 * @param element
	 */
	public void unCheck(WebElement element) {
		this.webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
		if (element.isSelected()) {
			element.click();
			logger.info("The checkbox " + element + " is Unchecked");
		} else {
			logger.info("The checkbox " + element + " is already Unchecked");
		}
	}

	/**
	 * Unselect a checkbox
	 *
	 * @param locator
	 */
	public void unCheck(String locator) {
		WebElement element = getElement(locator);
		unCheck(element);
	}

	/**
	 * Check or uncheck checkbox
	 *
	 * @param checkBox, enable
	 */
	public void checkUncheckCheckBox(WebElement checkBox, boolean enable) {
		if (enable && !isCheckBoxChecked(checkBox)) {
			clickJavaScript(checkBox);
		} else if (!enable && isCheckBoxChecked(checkBox)) {
			clickJavaScript(checkBox);
		}
	}

	/**
	 * Builds WebElement for any locator. Will return the WebElement when the element becomes available in the DOM
	 * This does not necessarily mean that the element is visible.
	 *
	 * @param locator
	 * locators can be based of xpath, class, css, name, linkText, tagName
	 * Will wait for a max of DEFAULT_WAIT_TIME defined in this class
	 *
	 * @return WebElement
	 */

	public WebElement getElement(String locator) {
		return getElement(locator, DEFAULT_WAIT_TIME);
	}

	/**
	 * Builds WebElement with any locator. Will return the WebElement when the element becomes available in the DOM
	 * This does not necessarily mean that the element is visible.
	 *
	 * @param locator
	 * @param maxTimeOut
	 * @return
	 */
	public WebElement getElement(String locator, long maxTimeOut) {
	    return getElement(getByFromLocator(locator), maxTimeOut);
	}

	/**
	 * private method not for use, internally used within this class
	 * for getElement, please use {@link #getElement(String) or #getElement(String, int)}
	 *
	 * @param by
	 * @param timeOut
	 * @return
	 */
	private WebElement getElement(By by, long timeOut) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
	    WebElement element = null;
	    try {
	        element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
	    } catch (TimeoutException e) {
            logger.info("getElement wait timed out for locator " + by.toString());
            return null;
        } catch (Exception e) {
            logger.error("Exception in finding element with locator " + by.toString());
            logger.info("Stacktrack:\n", e);
            return null;
        }
        logger.info("Element found with locator " + by.toString());
        return element;
	}

	/**
	 * Builds The List of WebElements By given locator strategy
	 *
	 * @param locator - locator strategy, id, name=sample, tag=div, link=sample
	 * @return WebElement
	 */
	public List<WebElement> getElements(String locator) {
		return getElements(locator, DEFAULT_WAIT_TIME);
	}

	/**
	 * Builds The List of WebElements By given locator strategy, and with a defined timeout
	 *
	 * @param locator
	 * @param timeOut
	 * @return
	 */

	public List<WebElement> getElements(String locator, long timeOut) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
	    List<WebElement> elementList = null;
	    try {
	        elementList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getByFromLocator(locator)));
	    } catch(TimeoutException te) {
	        logger.info("getElements timed out for locator " + locator);
	        return null;
	    } catch(Exception e) {
	        logger.error("Exception occured in getElements for locator " + locator);
	        e.printStackTrace();
	        return null;
	    }
	    logger.info("Elements found with locator " + locator);
	    return elementList;
	}

	/**
	 * Returns the WebElement By id
	 *
	 * @param id - Element HTML id
	 * @return WebElement whose HTML is id
	 */
	public WebElement getElementById(String id) {
		WebElement e;
		try {
			e = this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return e;
	}

	/**
	 * Returns list of Elements by id
	 *
	 * @param id
	 * @return WebElement
	 */
	public List<WebElement> getElementsById(String id) {
		List<WebElement> webElements = this.webDriverWait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
		return webElements;
	}

	/**
	 * Returns Element by name
	 *
	 * @param name
	 * @return WebElement
	 */
	public WebElement getElementByName(String name) {
		WebElement webElement = this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		return webElement;
	}

	/**
	 * Returns Element by LinkText
	 *
	 * @param LinkText
	 * @return WebElement
	 */
	public WebElement getElementByLinkText(String linkText) {
		WebElement webElement = this.webDriverWait
				.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
		return webElement;
	}

	/**
	 * Returns the WebElement By Xpath
	 *
	 * @param xpath - xpath of the element
	 * @return
	 */
	public WebElement getElementByXpath(String xpath) {
		WebElement e = null;
		try {
			e = this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			return e;
		} catch (TimeoutException ex) {
			return null;
		}
	}

	/**
	 * Returns list of WebElements By Xpath
	 *
	 * @param xpath - xpath of the element
	 * @return
	 */

	public List<WebElement> getElementsByXpath(String xpath) {
		List<WebElement> webElements = this.webDriverWait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));
		return webElements;
	}

	/**
	 * Returns the WebElement By ClassName
	 *
	 * @param className
	 * @return
	 */
	public WebElement getElementByClassName(String className) {
		WebElement e = null;
		try {
			e = this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
			return e;
		} catch (TimeoutException ex) {
			return null;
		}
	}

	/**
	 * Returns the WebElement By TagName
	 *
	 * @param tagName
	 * @return
	 */
	public WebElement getElementByTagName(String tagName) {
		WebElement e = null;
		try {
			e = this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(tagName)));
		} catch (TimeoutException ex) {
			logger.error("Element Was not presented...");
		}
		return e;
	}

	/**
	 * Returns the WebElement By CSS
	 *
	 * @param cssSelector
	 * @return
	 */
	public WebElement getElementByCSS(String cssSelector) {
		return this.webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
	}

	/**
	 * Get Div Element by text
	 *
	 * @param text
	 * @return
	 */
	public WebElement getElementByText(String text) {
		WebElement e = getElement("//div[text()='" + text + "']");
		return e;
	}

	/**
	 * Selects an element by double clicking on it
	 *
	 * @param elem - The element that needs to be selected
	 * browser - For Edge browser, there's a special way to doubleClick
	 *
	 */
	public void selectElement(WebElement elem, String browser) {
		if (browser.equals("Edge")) {
			String script = "var x = document.querySelector(\"#editor-content-editable > div > span\");"
					+ "if (x == null) x = document.querySelector(\"#editor-content-editable > div span\");"
					+ "window.getSelection().selectAllChildren(x);";
			executeScript(script);
		} else {
			Actions action = new Actions(driver);
			action.moveToElement(elem);
			action.doubleClick(elem).build().perform();
		}
	}

	/**
	 * MouseOver with webElement
	 * Web/hybrid context only.
	 *
	 * @param elem
	 */

	public void mouseOver(WebElement elem) {
		String script = "var obj = document.createEvent('MouseEvents'); "
				+ "obj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
				+ "arguments[0].dispatchEvent(obj);";
		executeScript(script, elem);
	}

	/**
	 * MouseOver using locator
	 *
	 * @param elem
	 */
	public void mouseOver(String locator) {
		WebElement element = getElement(locator);
		mouseOver(element);
	}

	/**
	 * mouse over using Actions class
	 *
	 * @param locator
	 */
	public void mouseOverByAction(String locator) {
		Actions action = new Actions(driver);
		WebElement element = getElement(locator);
		action.moveToElement(element).build().perform();
	}

	/**
	 *
	 * To find the element attribute. Attribute examples - is-disabled, active, checked
	 *
	 * @param element
	 * @param attribute
	 * @return
	 */
	public String getElementAttributeValue(WebElement element, String attribute) {
		return element.getAttribute(attribute);
	}

	/**
	 * Wait for a particular Page Title
	 * Web/hybrid context only.
	 *
	 * @param title - expected title
	 */
	public void waitForPageTitle(String title) {
		this.webDriverWait.until(ExpectedConditions.titleContains(title));
	}

	/**
	 * Wait for a particular Page URL
	 * Web/hybrid context only.
	 *
	 * @param url
	 */
	public void waitForPageURL(String url) {
		this.webDriverWait.until(ExpectedConditions.urlContains(url));
	}

	/**
	 * Wait For Element to disappear
	 *
	 * @param element
	 */
	public void waitForElementToDisappear(String locator) {
		this.webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(getByFromLocator(locator)));
	}

	/**
	 * Wait for element to disappear with locator, with a max timeout
	 *
	 * @param locator
	 * @param timeOut
	 */
	public void waitForElementToDisappear(String locator, int timeOut) {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
	    wait.until(ExpectedConditions.invisibilityOfElementLocated(getByFromLocator(locator)));
	}

	/**
     * Wait for element to disappear with locator, with a max timeout
     *
     * @param locator
     * @param timeOut
     */
    public void waitForElementToDisappear(WebElement element, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

	/**
	 * Waits for element to be clickable using WebElement
	 *
	 * @param element
	 */
	public void waitForElementToBeClickAble(WebElement element) {
		this.webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Waits for element to be clickable using locator
	 *
	 * @param locator
	 */
	public void waitForElementToBeClickAble(String locator) {
		this.webDriverWait.until(ExpectedConditions.elementToBeClickable(getByFromLocator(locator)));
	}

	/**
	 * Waits for element to be clickable using locator and a timeout defined
	 *
	 * @param locator
	 * @param maxTimeout - max time out till which the wait should happen
	 */
	public void waitForElementToBeClickAble(String locator, int maxTimeOut) {
	    WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(maxTimeOut));
	    driverWait.until(ExpectedConditions.elementToBeClickable(getByFromLocator(locator)));
	}

	/**
	 * Waits for element to be displayed and visible
	 * Visibility means that the element is not only present on the DOM but also has a height and width that is greater than 0.
	 *
	 * @param locator
	 */

	public void waitForElementToBeDisplayed(String locator) {
		this.webDriverWait.until(ExpectedConditions.visibilityOf(getElement(locator)));
	}

	/**
	 * Waits for element to be displayed or visible
	 * Visibility means that the element is not only present on the DOM but also has a height and width that is greater than 0.
	 *
	 * @param element - WebElement
	 */
	public void waitForElementToBeDisplayed(WebElement element) {
		this.webDriverWait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
     * Waits for element to be displayed or visible
     * Visibility means that the element is not only present on the DOM but also has a height and width that is greater than 0.
     *
     * @param element - WebElement
     */
    public boolean waitForElementToBeDisplayed(WebElement element, long maxTimeOut) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(maxTimeOut));
        WebElement elementFound = null;
        elementFound = webDriverWait.until(ExpectedConditions.visibilityOf(element));
        return elementFound != null;
    }

	/**
	 * Wait for the Element to be present. This method only checks
	 * if the Element is present, does not validate if the Element is visible
	 * For the Element to be visible use {@link #waitForElementToBeDisplayed(String)}
	 *
	 * @param locator
	 * @return boolean - if the Element was found or not
	 */

	public boolean waitForElementPresent(String locator) {
		return waitForElementPresent(locator, DEFAULT_WAIT_TIME);
	}

	/**
	 * Wait for the Element to be present. This method only checks
	 * if the Element is present in DOM, does not validate if the Element is visible
	 * For the Element to be visible use {@link #waitForElementToBeDisplayed(WebElement)}
	 *
	 * @param element
	 * @return boolean - if the Element was found or not
	 */
	public boolean waitForElementPresent(WebElement element) {
		return waitForElementPresent(element, DEFAULT_WAIT_TIME);
	}

	/**
	 * Wait for the Element to be present, with input parameter - maxTimeOut
	 *
	 * @param locator    see {@link #getByFromLocator(String)}
	 * @param maxTimeOut max seconds to wait
	 * @return true when found (otherwise the wait throws a TimeoutException)
	 */
	public boolean waitForElementPresent(String locator, long maxTimeOut) {
	    WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(maxTimeOut));
	    WebElement element = null;
	    element = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(getByFromLocator(locator)));
	    return element != null;
	}

	/**
	 * Wait for an element object, with input parameter - maxTimeOut.
	 * An element object (e.g. a page-object proxy) can only be checked by using it,
	 * so this waits until it is visible, which also proves it is present.
	 *
	 * @param element    the element
	 * @param maxTimeOut max seconds to wait
	 * @return true when visible (otherwise the wait throws a TimeoutException)
	 */
    public boolean waitForElementPresent(WebElement element, long maxTimeOut) {
    	WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(maxTimeOut));
	    WebElement elementFound = null;
	    elementFound = webDriverWait.until(ExpectedConditions.visibilityOf(element));
	    return elementFound != null;
     }

	/**
	 * Turns a locator string into a By.
	 *
	 * @param locator "//..." = XPath; "class=", "css=", "name=", "link=", "tag=" = that
	 *                strategy; anything else = element id (e.g. "com.app:id/button")
	 * @return By for the locator
	 */
	public By getByFromLocator(String locator) {
		try {
			if (locator.startsWith("//")) {
				return By.xpath(locator);
			} else if (locator.startsWith("class=")) {
				locator = locator.split("=")[1];
				return By.className(locator);
			} else if (locator.startsWith("css=")) {
				locator = locator.substring(4);
				return By.cssSelector(locator);
			} else if (locator.startsWith("name=")) {
				locator = locator.split("\\=")[1];
				return By.name(locator);
			} else if (locator.startsWith("link=")) {
				locator = locator.split("\\=")[1];
				return By.linkText(locator);
			} else if (locator.startsWith("tag=")) {
				locator = locator.split("\\=")[1];
				return By.tagName(locator);
			} else {
				return By.id(locator);
			}
		} catch (NoSuchElementException noSuchElementException) {
			return null;
		}
	}

	/**
	 * Verify if the element is present
	 *
	 * @param locator
	 * @return
	 */
    public boolean isElementPresent(String locator) {
        return isElementPresent(locator, Util.S_WAIT);
    }

    /**
	 * Verify if the element is present and timeOut defined
	 *
	 * @param locator
	 * @param timeOutInSeconds - max timeout to wait for the element to be present
	 * @return
	 */

	public boolean isElementPresent(String locator, int timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds));
        WebElement element = null;
        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(getByFromLocator(locator)));
        } catch(TimeoutException te) {
            logger.error("Wait timed out finding the element \n" + te.toString());
            logger.trace("Stacktrace:\n", te);
            return false;
        } catch (Exception e) {
            logger.error("Unknown Exception occured in finding element " + locator + "\n" + e.toString());
            logger.trace("Stacktrace: \n", e);
            return false;
        }
        if (element == null) {
            logger.error("Element is not present: " + locator);
            return false;
        }
        logger.info("Element is present: " + locator);
        return true;
    }

	/**
	 * Verify if the element is present
	 *
	 * @param element
	 * @return
	 */
	public boolean isElementPresent(WebElement element) {
		try {
			if (element != null) {
				logger.info("Element : " + element + " successfully appeared on page");
				return true;
			} else {
				logger.warn("Element : " + element + " is not present on page");
				return false;
			}
		} catch (Exception e) {
			logger.warn("Element : " + element + " is not present on page");
			return false;
		}
	}

	/**
	 * Opens a new tab
	 * Web/hybrid context only.
	 */
	public String openNewTab(String URL) {
		Util.sleepInSec(Util.M_WAIT, "Attempting to Open a New Tab");

		executeScript("window.open('','_blank');");

		ArrayList<String> tabList = new ArrayList<String>(driver.getWindowHandles());
		while (tabList.size() < 2) {
			logger.info("New Window Not Opened");
			executeScript("window.open('','_blank');");
			tabList = new ArrayList<String>(driver.getWindowHandles());
		}
		for (String tab : tabList) {
			driver.switchTo().window(tab);
			logger.info("URL is " + driver.getCurrentUrl());
			if (driver.getCurrentUrl().equals("about:blank")) {
				driver.get(URL);
				return URL;
			}
		}
		return "";
	}

	/**
	 * Clicks through Actions, for elements a plain click() cannot reach (e.g. SVG in a web view)
	 *
	 * @param elem the element
	 */
	public void clickOnSVGElement(WebElement elem) {
		Actions builder = new Actions(driver);
		builder.click(elem).build().perform();
	}

	/**
	 * Selects the New Browser Window
	 * Web/hybrid context only.
	 */
	public void selectBrowserPopUp() {
		// Get all the window handles in a set
		Set<String> handles = driver.getWindowHandles();
		while (handles.size() < 2) {
			Util.sleepInSec(1); // sleepInSec takes seconds: wait 1 s for the window to open
			handles = driver.getWindowHandles();
		}

		for (String handle : handles) {
			driver.switchTo().window(handle);
		}
	}

	/**
	 * Switch into an iframe by name. Web/hybrid context only.
	 *
	 * @param iFrame the frame's name attribute
	 */
	public void switchToIFrame(String iFrame) {
		waitForElementToBeDisplayed("//iframe[@name='" + iFrame + "']");
		driver.switchTo().frame(iFrame);
	}

	/**
	 * Switch into an iframe element. Web/hybrid context only.
	 *
	 * @param element the frame
	 */
	public void switchToIFrame(WebElement element) {
		driver.switchTo().frame(element);
	}

	/**
	 * Leave the current iframe. Web/hybrid context only.
	 */
	public void switchToParentFrame() {
		driver.switchTo().parentFrame();
	}

	/**
	 * Switch to the new Tab
	 * Web/hybrid context only.
	 */
	public void switchToNewTab() {
		tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
	}

	/**
	 * Switch to the parent tab
	 * Web/hybrid context only.
	 */
	public void switchToParentTab() {
		driver.close();
		driver.switchTo().window(tabs.get(0));
	}

	/**
	 * Closes the Browser Pop up window except if the Tile in ignoreWindowTitle
	 * Web/hybrid context only.
	 *
	 * @param windowHandle
	 * @param ignoreWindow
	 */
	public void closeBrowserPopUp(String windowHandle, String ignoreWindowTitle) {
		Set<String> handles = driver.getWindowHandles();
		for (String handle : handles) {
			driver.switchTo().window(handle);
			if (!handle.equals(windowHandle)) {
				if (!driver.getTitle().contains(ignoreWindowTitle))
					driver.close();
			}
		}
		driver.switchTo().window(windowHandle);

	}

	/**
	 * Select Option By Value
	 * Web/hybrid context only.
	 *
	 * @param element
	 * @param optionToSelect
	 */
	public void selectOptionByValue(WebElement element, String optionToSelect) {
		Select sel = new Select(element);
		sel.selectByValue(optionToSelect);
		logger.info("Selected the option : " + optionToSelect);
	}

	/**
	 * Selection Option By Value
	 *
	 * @param locator
	 * @param optionToSelect
	 */
	public void selectOptionByValue(String locator, String optionToSelect) {
		Select sel = new Select(getElement(locator));
		sel.selectByValue(optionToSelect);
		logger.info("Selected the option : " + optionToSelect);
	}

	/**
	 * Selection Option By Visible Text using locator
	 *
	 * @param locator
	 * @param optionToSelect
	 */

	public void selectOptionByVisibleText(String locator, String optionToSelect) {
		Select sel = new Select(getElement(locator));
		sel.selectByVisibleText(optionToSelect);
		logger.info("Selected the option : " + optionToSelect);
	}

	/**
	 * Selection Option By Visible Text using webelement
	 *
	 * @param locator
	 * @param optionToSelect
	 */

	public void selectOptionByVisibleText(WebElement element, String optionToSelect) {
		Select sel = new Select(element);
		sel.selectByVisibleText(optionToSelect);
		logger.info("Selected the option : " + optionToSelect);
	}

	/**
	 * get Selected drop down value
	 *
	 * @param element
	 * @return
	 */
	public String getSelectedDropDownValue(WebElement element) {
		Select sel = new Select(element);
		return sel.getFirstSelectedOption().getText();
	}

	/**
	 * Verifies if an option exists
	 *
	 * @param element
	 * @param optionToVerify - String that needs to be verified
	 */
	public boolean isOptionExists(WebElement element, String optionToVerify) {
		Select sel = new Select(element);
		boolean exists = false;
		List<WebElement> optList = sel.getOptions();
		for (int i = 0; i < optList.size(); i++) {
			String text = getText(optList.get(i));
			if (text.matches(optionToVerify)) {
				exists = true;
				break;
			}
		}
		if (exists) {
			logger.info("Option : " + optionToVerify + " exists in the element " + element);
		} else {
			logger.info("Option : " + optionToVerify + " does not exist in the element " + element);
		}
		return exists;
	}

	/**
	 * Take Screenshot
	 *
	 * @param issueType
	 */
	public void screenShot(String issueType) {
		String fileName = System.currentTimeMillis() + "." + issueType + ".png";
		File screenshotDir = new File("resources/screencapture");
		screenshotDir.mkdir();
		if (fileName.contains("/")) {
			fileName = fileName.replaceAll("/", "-");
			logger.info(fileName);
		}
		try {
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(screenshotDir + "//" + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Moves to location which is x_off, y_off pixel offset from top left corner of
	 * element and clicks on it Used to click outside of the modal typically.
	 *
	 * @param locator
	 * @param x_off
	 * @param y_off
	 */
	public void clickByElementOffset(String locator, int x_off, int y_off) {
		Actions action = new Actions(driver);
		action.moveToElement(getElement(locator), x_off, y_off).click().build().perform();
	}

	/**
	 * Navigate back
	 */
	public void navigateBack() {
		Util.sleepInSec(Util.M_WAIT, "Wait before navigating back");
		driver.navigate().back();
		Util.sleepInSec(Util.M_WAIT, "Wait for navigating back");
	}

	/**
	 * Navigate forward
	 */
	public void navigateForward() {
		Util.sleepInSec(Util.M_WAIT, "Wait before navigating forward");
		driver.navigate().forward();
		Util.sleepInSec(Util.M_WAIT, "Wait for navigating forward");
	}

	/**
	 * Do refresh the current browser session
	 * Web/hybrid context only.
	 */
	public void refreshUsingJavaScript() {
		executeJavaScript("window.location.reload(true);");
		Util.sleepInSec(Util.M_WAIT, "Wait for page load");
	}

	/**
	 * Drag and drop source element to target element
	 *
	 * @param source locator of the element to drag
	 * @param target locator of the drop target
	 */
	public void dragAndDrop(String source, String target) {
		Actions actions = new Actions(driver);
		actions.dragAndDrop(getElement(source), getElement(target)).build().perform();
	}

	/**
	 * Right click (long-press on touch screens) on element
	 *
	 * @param locator see {@link #getByFromLocator(String)}
	 */
	public void rightClick(String locator) {
		Actions actions = new Actions(driver);
		actions.contextClick(getElement(locator)).build().perform();
	}

	/**
	 * Scroll for element present
	 *
	 * @param locator
	 */
	public void scrollForElementPresent(String locator, String scrollLocator) {
		int count = 0;
		while (!isElementPresent(locator) && count <= 20) {
			scrollDown(getElement(scrollLocator));
			Util.sleepInSec(Util.S_WAIT, "Wait for load items");
			count++;
		}
	}

	/**
	 * Verify alert presense
	 *
	 * @return
	 */
	public boolean isAlertPresents() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Maximizes window size
	 * Web/hybrid context only.
	 */
	public void maximizeBrowserWindown() {
		driver.manage().window().maximize();
	}

	/**
	 * Returns the second capture group of a regex match.
	 *
	 * @param pattern   regex with at least two groups
	 * @param attribute text to search
	 * @return the text of group 2 of the first match
	 */
	public String getStringPatternMatcher(String pattern, String attribute) {
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(attribute);
		matcher.find();
		return matcher.group(2);
	}

	/**
	 * Press key combo, e.g. CONTROL + A
	 *
	 * @param mainKey    key held down
	 * @param supportKey key pressed while mainKey is down
	 */
	public void keyTouchCombo(Keys mainKey, Keys supportKey) {
		Actions action = new Actions(driver);
		action.keyDown(mainKey).sendKeys(supportKey).keyUp(mainKey).build().perform();
	}

	/**
	 * Press key combo multiple times
	 *
	 * @param mainKey    key held down
	 * @param supportKey key pressed while mainKey is down
	 * @param count      how many times
	 */
	public void keyTouchCombo(Keys mainKey, Keys supportKey, int count) {
		for (int i = 0; i < count; i++) {
			keyTouchCombo(mainKey, supportKey);
		}
	}

	/**
     * Drag and drop source element to target element
     *
     * @param source element to drag
     * @param target drop target
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        Actions actions = new Actions(driver);
        actions.dragAndDrop(source, target).build().perform();
    }
}
