package com.qaautomation.steps;

import com.qaautomation.driver.DriverFactory;
import com.qaautomation.pages.wit.RegistrationModal;
import com.qaautomation.pages.wit.WitLandingPage;
import com.qaautomation.utils.LinkChecker;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WitLandingPageSteps {

    private WitLandingPage page() {
        return new WitLandingPage(DriverFactory.getDriver());
    }

    private RegistrationModal modal() {
        return new RegistrationModal(DriverFactory.getDriver());
    }

    @Given("I open the Women in Tech landing page")
    public void iOpenTheLandingPage() {
        page().open();
    }

    @Given("I am using a phone-sized screen")
    public void iAmUsingAPhoneSizedScreen() {
        DriverFactory.getDriver().manage().window().setSize(new Dimension(390, 844));
    }

    // ---------- Page content ----------

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expected) {
        String title = page().getPageTitle();
        assertTrue(title.contains(expected), "Page title was: " + title);
    }

    @Then("the main heading should contain {string}")
    public void theMainHeadingShouldContain(String expected) {
        String heading = page().getHeroTitle();
        assertTrue(heading.toLowerCase().contains(expected.toLowerCase()), "Heading was: " + heading);
    }

    @Then("the top navigation should have these links:")
    public void theTopNavigationShouldHaveTheseLinks(List<String> expected) {
        List<String> actual = page().getNavLinkTexts().stream()
                .map(text -> text.replaceAll("\\s+", " ").trim())
                .toList();
        assertEquals(expected, actual);
    }

    @Then("every navigation link should point to a section that exists")
    public void everyNavLinkShouldPointToASection() {
        WitLandingPage page = page();
        List<String> missing = new ArrayList<>();
        for (String target : page.getNavLinkTargets()) {
            String id = target.substring(target.indexOf('#') + 1);
            if (!page.hasSectionWithId(id)) {
                missing.add(target);
            }
        }
        assertTrue(missing.isEmpty(), "Nav links with no matching section: " + missing);
    }

    @Then("every speaker card should show the speaker's name")
    public void everySpeakerCardShouldShowTheName() {
        WitLandingPage page = page();
        List<WebElement> cards = page.getSpeakerCards();
        assertFalse(cards.isEmpty(), "No speaker cards found");
        for (WebElement card : cards) {
            assertFalse(page.getSpeakerName(card).isEmpty(), "A speaker card has no name");
        }
    }

    @Then("every speaker card should have a LinkedIn link")
    public void everySpeakerCardShouldHaveALinkedInLink() {
        WitLandingPage page = page();
        List<String> missing = new ArrayList<>();
        for (WebElement card : page.getSpeakerCards()) {
            if (!page.getSpeakerLinkedInUrl(card).contains("linkedin.com")) {
                missing.add(page.getSpeakerName(card));
            }
        }
        assertTrue(missing.isEmpty(), "Speakers with no LinkedIn link: " + missing);
    }

    @Then("there should be a visible button to register")
    public void thereShouldBeAVisibleButtonToRegister() {
        assertTrue(page().hasVisibleRegisterButton(),
                "No visible Reserve Seat / register button - visitors can't open the registration form");
    }

    // ---------- Registration form ----------

    @When("I open the registration form")
    public void iOpenTheRegistrationForm() {
        page().openRegistration();
    }

    @When("I close the registration form")
    public void iCloseTheRegistrationForm() {
        modal().close();
    }

    @When("I press the Escape key")
    public void iPressTheEscapeKey() {
        modal().pressEscape();
    }

    @Then("the registration form should be open")
    public void theRegistrationFormShouldBeOpen() {
        assertTrue(modal().isOpen(), "Registration form is not open");
    }

    @Then("the registration form should be closed")
    public void theRegistrationFormShouldBeClosed() {
        assertFalse(modal().isOpen(), "Registration form is still open");
    }

    @Then("the cursor should be in the {string} field")
    public void theCursorShouldBeInTheField(String fieldId) {
        assertTrue(modal().waitForFocusOn(fieldId), "Focus did not move to the " + fieldId + " field");
    }

    @When("I enter name {string} and email {string}")
    public void iEnterNameAndEmail(String name, String email) {
        modal().enterDetails(name, email);
    }

    @When("I try to submit the registration")
    public void iTryToSubmitTheRegistration() {
        modal().submitInvalidForm();
    }

    @Then("the {string} field should show a validation error")
    public void theFieldShouldShowAValidationError(String field) {
        assertNotEquals("", modal().getValidationMessage(field), "No validation error on field: " + field);
    }

    @Then("the registration should not be sent")
    public void theRegistrationShouldNotBeSent() {
        RegistrationModal modal = modal();
        assertTrue(modal.isOpen(), "Form closed - it may have been sent");
        assertFalse(modal.isSuccessMessageShown(), "Success message shown for an invalid form");
        assertFalse(modal.getSubmitButtonText().contains("Sending"), "Form started sending");
    }

    @Then("the audience dropdown should list:")
    public void theAudienceDropdownShouldList(List<String> expected) {
        assertEquals(expected, modal().getAudienceOptions());
    }

    // ---------- FAQ and mobile menu ----------

    @Then("the FAQ should have {int} questions")
    public void theFaqShouldHaveQuestions(int expected) {
        assertEquals(expected, page().getFaqCount());
    }

    @When("I click FAQ question {int}")
    public void iClickFaqQuestion(int number) {
        page().clickFaqQuestion(number - 1);
    }

    @Then("the answer to FAQ question {int} should be shown")
    public void theFaqAnswerShouldBeShown(int number) {
        assertTrue(page().isFaqAnswerShown(number - 1), "FAQ answer " + number + " is hidden");
    }

    @Then("the answer to FAQ question {int} should be hidden")
    public void theFaqAnswerShouldBeHidden(int number) {
        assertFalse(page().isFaqAnswerShown(number - 1), "FAQ answer " + number + " is still shown");
    }

    @When("I open the mobile menu")
    public void iOpenTheMobileMenu() {
        page().openMobileMenu();
    }

    @When("I close the mobile menu")
    public void iCloseTheMobileMenu() {
        page().closeMobileMenu();
    }

    @Then("the mobile menu should be visible")
    public void theMobileMenuShouldBeVisible() {
        assertTrue(page().isMobileMenuOpen(), "Mobile menu did not open");
    }

    @Then("the mobile menu should be hidden")
    public void theMobileMenuShouldBeHidden() {
        assertFalse(page().isMobileMenuOpen(), "Mobile menu did not close");
    }

    // ---------- Quality checks (these found the bugs in docs/BUG_REPORTS.md) ----------

    @Then("no link to another page on the site should be broken")
    public void noInternalLinkShouldBeBroken() {
        Map<String, Integer> broken = LinkChecker.findBrokenLinks(page().getInternalPageLinks());
        assertTrue(broken.isEmpty(), "Broken links (url=status): " + broken);
    }

    @Then("the countdown should not show {string} after {string}")
    public void theCountdownShouldNotShowAfter(String text, String eventDate) {
        if (!LocalDate.now().isAfter(LocalDate.parse(eventDate))) {
            return; // Event hasn't happened yet, so "LIVE" may be correct.
        }
        assertNotEquals(text, page().getCountdownMinutes(),
                "Countdown still shows '" + text + "' although the event was on " + eventDate);
    }

    @Then("the FAQ should not mention {string}")
    public void theFaqShouldNotMention(String text) {
        assertFalse(page().getFaqText().toLowerCase().contains(text.toLowerCase()),
                "FAQ still says '" + text + "'");
    }

    @Then("every image should have alt text")
    public void everyImageShouldHaveAltText() {
        List<String> missing = page().getImagesWithoutAltText();
        assertTrue(missing.isEmpty(), missing.size() + " images have no alt text: " + missing);
    }

    @Then("the meta description should be at least {int} characters long")
    public void theMetaDescriptionShouldBeAtLeast(int minLength) {
        String description = page().getMetaDescription();
        assertTrue(description.length() >= minLength,
                "Meta description is only " + description.length() + " characters: '" + description + "'");
    }
}
