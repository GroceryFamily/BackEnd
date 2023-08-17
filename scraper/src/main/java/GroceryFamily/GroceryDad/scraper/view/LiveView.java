package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.model.Link;
import com.codeborne.selenide.SelenideDriver;
import lombok.experimental.SuperBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@SuperBuilder
public abstract class LiveView {
    protected SelenideDriver driver;

    public final String open(Link link, Duration timeout) {
        driver.open(link.url);
        waitUntilPageReady(timeout);
        initialize(link);
        return driver.$("html").innerHtml();
    }

    protected abstract void initialize(Link link);

    private void waitUntilPageReady(Duration timeout) {
        new WebDriverWait(driver.getWebDriver(), timeout).until(LiveView::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }
}