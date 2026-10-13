package com.mobile.automation.ui.selenium;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.eclipse.jetty.util.StringUtil;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.mobile.automation.core.ConfigReader;

import io.appium.java_client.AppiumDriver;

/**
 * This class is the base page for all the web page classes All the common
 * functions that can be used across all web pages are defined here. Example -
 * enableFeatureFlip When a new pageClass is created, please extend this class
 * 
 * For more webPage specific functions, please visit the respective Page classes
 * in the package - com.mobile.automation.pagehelpermethods For more functions
 * to perform UI actions, please go to {@link DriverActions}
 * 
 */
public class BasePage extends DriverActions {

    protected Properties properties = new Properties();
    private InputStream fileInputStream;
    protected FileReader fileReader;
    protected JSONObject json;
    int value = 0;

    public BasePage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isLanguage() {
        if (StringUtil.isNotBlank(ConfigReader.getConfiguration(AppiumConfigs.WEB_LANGUAGE_PROPERTY,
                AppiumConfigs.WEB_LANGUAGE_DEFAULT_VALUE)))
            return true;
        else
            return false;
    }

    public String getLanguage() {
        return ConfigReader.getConfiguration(AppiumConfigs.WEB_LANGUAGE_PROPERTY,
                AppiumConfigs.WEB_LANGUAGE_DEFAULT_VALUE);
    }

    public void loadProperties(String fileName) {
        try {
            fileInputStream = new FileInputStream(
                    System.getProperty("user.dir") + "//resources//TestFiles//" + fileName + ".properties");
            properties.load(fileInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getKeyValue(String key) {
        return properties.getProperty(key);
    }

    public void closeProperties() {

        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
