package com.qaautomation.pages;

import com.qaautomation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Parent class for every page object.
 * Holds the shared helpers so each page only describes its own elements and actions.
 * All helpers use explicit waits instead of Thread.sleep, which keeps tests fast and stable.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.seconds")));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Scrolls an element to the middle of the screen. Needed for content that only
     * fades in once the visitor scrolls to it - until then Selenium treats it as hidden.
     */
    protected void scrollIntoView(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", element);
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    /** Checks right now, without waiting. Useful for elements that may not exist. */
    protected boolean isDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    /**
     * Reads text even from elements that are faded out (opacity 0) by scroll animations.
     * Selenium's getText() returns "" for those because it only reads what is visible.
     */
    protected String getTextContent(By locator) {
        return driver.findElement(locator).getDomProperty("textContent").trim();
    }

    protected List<String> getAllTexts(By locator) {
        return driver.findElements(locator).stream()
                .map(WebElement::getText)
                .toList();
    }
}
