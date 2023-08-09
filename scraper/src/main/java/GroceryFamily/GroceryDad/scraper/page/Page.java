package GroceryFamily.GroceryDad.scraper.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.lang.String.format;

public class Page {
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

    public static String decodeUrl(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public static Condition hrefContains(String value) {
        return attributeMatching("href", format(".*%s.*", value));
    }

    public static Condition textContains(String value) {
        return matchText(format(".*%s.*", value));
    }

    public static Condition number() {
        return matchText("[0-9]+");
    }

    public static Condition number(Number number) {
        return text(number.toString());
    }
}