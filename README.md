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
CalculatorTestSuite.xml  (platform = Android)
  -> BaseTestSuite        starts the driver (DriverFactory) and picks the platform
  -> PlatformController   returns Android or IOS, both implement Platform
  -> CalculatorPage       page actions (pagehelpermethods)
  -> CalculatorPageFactory  locators only (elementrepository, @AndroidFindBy)
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
| `resources/config/ios.properties` | `getIosInstance()` (XCUITest, needs a Mac; not run yet) |

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

## Known limitations (next steps)

- iOS: capabilities are ready in `ios.properties`, but there is no iOS app or page object yet (`IOS.getCalculatorPage()` returns null).
- The driver and page elements are static, so tests cannot run in parallel yet.
