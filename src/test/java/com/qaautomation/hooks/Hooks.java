package com.qaautomation.hooks;

import com.qaautomation.driver.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/** Runs before and after every scenario: a fresh browser each time, screenshot on failure. */
public class Hooks {

    @Before
    public void startBrowser() {
        DriverFactory.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && DriverFactory.hasDriver()) {
            byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot at failure");
        }
        DriverFactory.quitDriver();
    }
}
