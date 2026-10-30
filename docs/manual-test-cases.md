# Manual test cases

The test case this framework automates, written as a tester would run it by hand on an Android
emulator or an iOS simulator. The TestNG method that covers it starts with its ID
(`CalculatorTest.tc101_testCal`) and so does its description (`[TC-101] 2 + 4 = shows 6`), so a
failure in the report leads straight to the case below.

Not listed: `ReportDemoTest`, which fails and skips on purpose to show the report.

## Setup

- Android: the calculator app from `resources/config/android.properties` (suite
  `CalculatorTestSuite.xml`). iOS: the app from `resources/config/ios.properties`
  (suite `CalculatorTestSuite-iOS.xml`, Mac only).

## Summary

| ID | Title | Type | Priority | Platforms |
|---|---|---|---|---|
| TC-101 | 2 + 4 = shows 6 | Smoke | High | Android, iOS |

---

### TC-101 2 + 4 = shows 6

| # | Step | Expected result |
|---|---|---|
| 1 | Start the calculator app | Keypad and result field shown |
| 2 | Tap **2**, **+**, **4**, **=** | The result field shows `6` |
