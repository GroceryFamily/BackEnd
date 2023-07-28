package GroceryFamily.GroceryDad;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@SpringBootApplication
public class RimiScraper implements CommandLineRunner {
    public static void main(String... args) {
        SpringApplication.run(RimiScraper.class, args);
    }

    final WebDriver driver;

    RimiScraper(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {
        using(driver, () -> {
            open("https://rimi.ee/epood/en");
            useOnlyStrictlyNecessaryCookies();
            category("Groceries", "All category products");
        });
    }

    static void useOnlyStrictlyNecessaryCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    static void category(String... names) {
        if (names.length == 0) return;
        $("#desktop_category_menu_button").click();
        var name = names[0];
        var button = $$("#desktop_category_menu button").findBy(text(name));
        var submenu = button.attr("aria-owns");
        button.click();
        for (int idx = 1; idx < names.length; ++idx) {
            name = names[idx];
            button = $$("#" + submenu + " a").findBy(text(name));
            submenu = button.attr("aria-owns");
            button.click();
        }
    }
}