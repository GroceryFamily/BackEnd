package GroceryFamily.GroceryDad.scraper.page;

import com.codeborne.selenide.Selenide;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class PageUtils {
    public static Duration sleepDelay = Duration.ofSeconds(1);

    public static void sleep() {
        Selenide.sleep((long) (sleepDelay.toMillis() * (1 + Math.random())));
    }

    public static void scrollUp() {
        executeJavaScript("scroll(0, 0);");
    }

    public static void scrollDown() {
        executeJavaScript("scroll(0, document.body.scrollHeight);");
    }
}