package com.mobile.automation.ui.selenium;

public class Constants {
    
    // Test mailbox for ReadEmails; pass real values as -Dmail.user=... -Dmail.password=... (never commit them)
    public static final String GMAIL_EMAIL_USER_NAME = System.getProperty("mail.user", "your-test-account@gmail.com");
    public static final String GMAIL_EMAIL_USER_PASS = System.getProperty("mail.password", "");
    public static final String EXCEL_DATA_TEMPLATE = "/resources/testfiles/Test Data Sheet.xlsx";
    
}
