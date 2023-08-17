package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.model.Link;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import lombok.experimental.SuperBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@SuperBuilder
public abstract class LiveView {
    protected SelenideDriver driver;
    protected Duration timeout;
    protected Duration sleepDelay;

    public final String open(Link link) {
        driver.open(link.url);
        waitUntilPageReady();
        initialize(link);
        return driver.$("html").innerHtml();
    }

    protected abstract void initialize(Link link);

    private void waitUntilPageReady() {
        new WebDriverWait(driver.getWebDriver(), timeout).until(LiveView::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    protected final void sleep() {
        Selenide.sleep((long) (sleepDelay.toMillis() * (1 + Math.random())));
    }

    protected final void scrollUp() {
        driver.executeJavaScript("scroll(0, 0);");
    }

    protected final void scrollDown() {
        driver.executeJavaScript("scroll(0, document.body.scrollHeight);");
    }
}