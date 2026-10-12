package com.qaautomation.pages.wit;

import com.qaautomation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Map;

/**
 * The "Reserve My Seat" popup form.
 * The site is live, so tests only check validation and never send a real registration.
 */
public class RegistrationModal extends BasePage {

    private final By modal = By.id("witModal");
    private final By form = By.id("witForm");
    private final By nameInput = By.id("wit-name");
    private final By emailInput = By.id("wit-email");
    private final By audienceSelect = By.id("wit-audience");
    private final By submitButton = By.id("witSubmit");
    private final By closeButton = By.cssSelector("[data-wit-close]");
    private final By successMessage = By.id("witSuccess");

    private final Map<String, By> fields = Map.of(
            "name", nameInput,
            "email", emailInput);

    public RegistrationModal(WebDriver driver) {
        super(driver);
    }

    public void waitUntilOpen() {
        wait.until(d -> isOpen());
    }

    public boolean isOpen() {
        return !Boolean.parseBoolean(driver.findElement(modal).getDomProperty("hidden"));
    }

    public void close() {
        click(closeButton);
        wait.until(d -> !isOpen());
    }

    public void pressEscape() {
        driver.findElement(nameInput).sendKeys(Keys.ESCAPE);
        wait.until(d -> !isOpen());
    }

    /** The page moves focus into the form a moment after it opens, so wait for it. */
    public boolean waitForFocusOn(String fieldId) {
        try {
            wait.until(d -> fieldId.equals(d.switchTo().activeElement().getDomAttribute("id")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterDetails(String name, String email) {
        type(nameInput, name);
        type(emailInput, email);
    }

    /**
     * Clicks "Reserve My Seat", but only when the form is invalid.
     * A valid form would send a real registration email, so that is refused.
     */
    public void submitInvalidForm() {
        WebElement formElement = driver.findElement(form);
        boolean valid = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", formElement);
        if (valid) {
            throw new IllegalStateException("Refusing to submit a valid registration to the live site");
        }
        click(submitButton);
    }

    /** The browser's built-in message, e.g. "Please fill in this field." Empty when the field is valid. */
    public String getValidationMessage(String field) {
        By locator = fields.get(field);
        if (locator == null) {
            throw new IllegalArgumentException("Unknown field: " + field + ". Use one of " + fields.keySet());
        }
        return driver.findElement(locator).getDomProperty("validationMessage");
    }

    public boolean isSuccessMessageShown() {
        return driver.findElement(successMessage).getDomAttribute("class").contains("show");
    }

    public String getSubmitButtonText() {
        return getText(submitButton);
    }

    /** Dropdown options, without the "Select…" placeholder. */
    public List<String> getAudienceOptions() {
        return new Select(waitForVisible(audienceSelect)).getOptions().stream()
                .filter(option -> !option.getDomProperty("value").isEmpty())
                .map(WebElement::getText)
                .toList();
    }
}
