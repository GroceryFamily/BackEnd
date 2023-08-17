package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Link;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import lombok.experimental.SuperBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuperBuilder
public abstract class LiveView {
    protected final SelenideDriver driver;
    protected final GroceryDadConfig.Scraper.Live config;

    public final String open(Link link) {
        driver.open(link.url);
        waitUntilPageReady();
        initialize(link);
        sleep();
        return driver.$("html").innerHtml();
    }

    protected abstract void initialize(Link link);

    private void waitUntilPageReady() {
        new WebDriverWait(driver.getWebDriver(), config.waitTimeout).until(LiveView::pageIsReady);
    }

    private static boolean pageIsReady(WebDriver driver) {
        return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
    }

    protected final void sleep() {
        Selenide.sleep((long) (config.sleepDelay.toMillis() * (1 + Math.random())));
    }

    protected final void scrollUp() {
        driver.executeJavaScript("scroll(0, 0);");
    }

    protected final void scrollDown() {
        driver.executeJavaScript("scroll(0, document.body.scrollHeight);");
    }
}