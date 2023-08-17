package GroceryFamily.GroceryDad.scraper.view.barbora;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.view.LiveView;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@SuperBuilder
class BarboraLiveView extends LiveView {
    private static boolean initialized;

    @Override
    public void initialize(Link link) {
        if (!initialized) {
            declineAllCookies();
            switchToEnglish();
            initialized = true;
        }
    }

    private void declineAllCookies() {
        driver.$("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    private void switchToEnglish() {
        topMenuItem("Kaubavalik").shouldBe(visible);
        languageSelectDropdown().shouldBe(visible).hover();
        sleep();
        englishLanguageOption().shouldBe(visible).hover();
        sleep();
        englishLanguageOption().shouldBe(visible).click();
        topMenuItem("Products").shouldBe(visible);
    }

    private SelenideElement topMenuItem(String name) {
        return topMenu().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    private SelenideElement topMenu() {
        return driver.$("#desktop-menu-placeholder");
    }

    private SelenideElement englishLanguageOption() {
        return languageSelectDropdown().$$("li").findBy(text("English"));
    }

    private SelenideElement languageSelectDropdown() {
        return driver.$("#fti-header-language-dropdown");
    }
}