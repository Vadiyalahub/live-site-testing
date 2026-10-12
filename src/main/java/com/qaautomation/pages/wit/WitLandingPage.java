package com.qaautomation.pages.wit;

import com.qaautomation.config.ConfigReader;
import com.qaautomation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;
import java.util.List;

/** The Women in Tech Series 2026 event landing page. */
public class WitLandingPage extends BasePage {

    private final By heroTitle = By.tagName("h1");
    private final By navLinks = By.cssSelector("nav.topnav a");
    private final By reserveSeatButton = By.cssSelector("a.wit-register");
    private final By faqItems = By.cssSelector("#faq details");
    private final By faqQuestion = By.tagName("summary");
    private final By countdownMinutes = By.cssSelector("#cd [data-k='m']");
    private final By speakerCards = By.cssSelector("#speakers article.speaker");
    private final By speakerPhoto = By.tagName("img");
    private final By speakerLinkedIn = By.cssSelector("a.li");
    private final By hamburgerButton = By.id("hamburger");
    private final By mobileMenu = By.id("mobileNav");
    private final By mobileMenuClose = By.id("mnavClose");
    private final By images = By.tagName("img");
    private final By links = By.cssSelector("a[href]");
    private final By metaDescription = By.cssSelector("meta[name='description']");

    public WitLandingPage(WebDriver driver) {
        super(driver);
    }

    public WitLandingPage open() {
        driver.get(ConfigReader.get("wit.url"));
        waitForVisible(heroTitle);
        // The site animates scrolling. Selenium would click while the page is still moving, so turn it off.
        ((JavascriptExecutor) driver).executeScript("document.documentElement.style.scrollBehavior = 'auto';");
        return this;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    /** The heading is split over several lines on screen, so join it into one line. */
    public String getHeroTitle() {
        return getText(heroTitle).replaceAll("\\s+", " ");
    }

    public List<String> getNavLinkTexts() {
        waitForVisible(navLinks);
        return getAllTexts(navLinks);
    }

    /** Returns the raw href of each nav link, e.g. "#speakers". */
    public List<String> getNavLinkTargets() {
        return driver.findElements(navLinks).stream()
                .map(link -> link.getDomAttribute("href"))
                .toList();
    }

    public boolean hasSectionWithId(String id) {
        return !driver.findElements(By.id(id)).isEmpty();
    }

    public RegistrationModal openRegistration() {
        // The button sits in a card that fades in on scroll, like a real visitor would see it.
        scrollIntoView(reserveSeatButton);
        click(reserveSeatButton);
        RegistrationModal modal = new RegistrationModal(driver);
        modal.waitUntilOpen();
        return modal;
    }

    public int getFaqCount() {
        return driver.findElements(faqItems).size();
    }

    public void clickFaqQuestion(int index) {
        WebElement question = driver.findElements(faqItems).get(index).findElement(faqQuestion);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", question);
        wait.until(ExpectedConditions.elementToBeClickable(question)).click();
    }

    public boolean isFaqAnswerShown(int index) {
        return Boolean.parseBoolean(driver.findElements(faqItems).get(index).getDomProperty("open"));
    }

    public String getCountdownMinutes() {
        return getTextContent(countdownMinutes);
    }

    public List<WebElement> getSpeakerCards() {
        wait.until(ExpectedConditions.presenceOfElementLocated(speakerCards));
        return driver.findElements(speakerCards);
    }

    public String getSpeakerPhotoAlt(WebElement card) {
        String alt = card.findElement(speakerPhoto).getDomAttribute("alt");
        return alt == null ? "" : alt.trim();
    }

    public String getSpeakerLinkedInUrl(WebElement card) {
        List<WebElement> link = card.findElements(speakerLinkedIn);
        return link.isEmpty() ? "" : link.get(0).getDomAttribute("href");
    }

    public void openMobileMenu() {
        click(hamburgerButton);
        wait.until(d -> isMobileMenuOpen());
    }

    public void closeMobileMenu() {
        click(mobileMenuClose);
        wait.until(d -> !isMobileMenuOpen());
    }

    public boolean isMobileMenuOpen() {
        return !Boolean.parseBoolean(driver.findElement(mobileMenu).getDomProperty("hidden"));
    }

    /** All text on the page, including sections that are still faded out. */
    public String getPageText() {
        return (String) ((JavascriptExecutor) driver).executeScript("return document.body.innerText;");
    }

    public List<String> getImagesWithoutAltText() {
        return driver.findElements(images).stream()
                .filter(img -> {
                    String alt = img.getDomAttribute("alt");
                    return alt == null || alt.isBlank();
                })
                .map(img -> img.getDomProperty("src"))
                .toList();
    }

    public String getMetaDescription() {
        List<WebElement> meta = driver.findElements(metaDescription);
        return meta.isEmpty() ? "" : meta.get(0).getDomAttribute("content");
    }

    /** Unique links to other pages on the same website (anchors like #faq are ignored). */
    public List<String> getInternalPageLinks() {
        String host = URI.create(ConfigReader.get("wit.url")).getHost();
        return driver.findElements(links).stream()
                .map(link -> link.getDomProperty("href"))
                .filter(href -> href != null && href.startsWith("http"))
                .map(href -> href.split("#")[0])
                .filter(href -> host.equals(URI.create(href).getHost()))
                .distinct()
                .toList();
    }
}
