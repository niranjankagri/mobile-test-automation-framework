# Mobile Test Automation Framework

Appium + Java + TestNG framework for Android (iOS prepared), first built in 2021 and
upgraded in 2026 to current versions. Work in progress: the framework is being extended.

## Stack

| Part | Version |
|---|---|
| Java | 17 (builds and runs on newer JDKs) |
| Appium Java client | 11.0.0 |
| Selenium support | 4.51.0 |
| TestNG | 7.12.0 |
| Appium server / driver | 3.x / UiAutomator2 |
| Reports | custom emailable HTML report, Extent Reports 2, log4j2 |

## Design

```
CalculatorTestSuite.xml (platform = Android) / CalculatorTestSuite-iOS.xml (platform = iOS)
  -> BaseTestSuite        starts the driver (DriverFactory, <platform>.properties) and picks the platform
  -> PlatformController   returns Android or IOS, both implement Platform
  -> Calculator           what tests call; one implementation per platform:
       pagehelpermethods/android/CalculatorPage   -> elementrepository/android (@AndroidFindBy)
       pagehelpermethods/ios/CalculatorPage       -> elementrepository/ios (@iOSXCUITFindBy)
  -> DriverActions / BasePage  shared waits and element actions
TestListener: custom HTML report, retry of failed tests (RetryFailedTests), Extent report
```

Tests ask `app.getCalculatorPage()` and do not know which operating system they run on.

## App under test

Google Calculator (`com.google.android.calculator`). The APK is not part of this repository.
Download it (for example from APKMirror) and save it as:

```
resources/apps/calculator.apk
```

## Capabilities

Each platform has its own file, read by `DriverFactory`:

| File | Used by |
|---|---|
| `resources/config/android.properties` | `getAndroidInstance()` (UiAutomator2) |
| `resources/config/ios.properties` | `getIosInstance()` (XCUITest, needs a Mac; not run yet; simulator and real-device settings explained in the file) |

`appium.server.url` is the server; every `capability.<name>` line is one capability
(`capability.platformVersion=15` is sent as `appium:platformVersion`). Override any key for one run:

```
mvn test -Dsurefire.suiteXmlFiles=resources/suites/CalculatorTestSuite.xml -Dcapability.deviceName=emulator-5556
```

## Run

1. Start an Android emulator (`android.properties` expects Android 15, `emulator-5554`).
2. Start the Appium server: `appium` (default address `http://127.0.0.1:4723`).
3. Run the suite:

```
mvn test -Dsurefire.suiteXmlFiles=resources/suites/CalculatorTestSuite.xml
```

Reports and logs:

| Output | What |
|---|---|
| `target/surefire-reports/custom-emailable-report.html` | dashboard, per-class and per-method results, stack traces |
| `test-output/extent-report.html` | Extent report, one entry per test class |
| `resources/screenshots/<test>.png` | device screen of every failed test |
| `target/taf-default.log`, `target/android-default.log` | full log, and the framework's lines per platform |

A failed test is retried once (`RetryFailedTests`); only the final attempt counts.

## Run on iOS (Mac only)

The same `CalculatorTest` runs on iOS; only the platform parameter differs.
iOS page objects: `elementrepository/ios/CalculatorPageFactory` (locators) and
`pagehelpermethods/ios/CalculatorPage` (actions), both behind the `Calculator` interface.

1. Install Xcode, open it once, accept the licence and install a simulator runtime.
2. `appium driver install xcuitest`
3. Put the app in `resources/apps` (`.app` for the simulator, `.ipa` for a device) and set
   `capability.app`, `platformVersion` and `deviceName` in `ios.properties`
   (`xcrun simctl list devices` lists the simulators).
4. Check the locators with Appium Inspector: the iOS ids in the repo are examples and
   have not been run yet.
5. `appium`, then:

```
mvn test -Dsurefire.suiteXmlFiles=resources/suites/CalculatorTestSuite-iOS.xml
```

The first run builds WebDriverAgent onto the simulator and takes a few minutes.

## Report demo (a failure and a skip on purpose)

`ReportDemoTest` has one test that fails on purpose (2 + 4 checked against 7) and one that is
skipped on purpose (`SkipException`). They run only in their own suite, so the normal suite stays green:

```
mvn test -Dsurefire.suiteXmlFiles=resources/suites/ReportDemoSuite.xml
```

Expected: `Tests run: 3, Failures: 1, Errors: 0, Skipped: 1` and BUILD FAILURE. The failed test is
retried once first. Then open `target/surefire-reports/custom-emailable-report.html` (pie chart
1 / 1 / 1, the failure with screenshot and stack trace) and `resources/screenshots/failOnPurpose.png`.

## Known limitations (next steps)

- iOS: session settings, page objects and suite are in place, but nothing has run on iOS yet (needs a Mac); the iOS locators are examples.
- The driver and page elements are static, so tests cannot run in parallel yet.
