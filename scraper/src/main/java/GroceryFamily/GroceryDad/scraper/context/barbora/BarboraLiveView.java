package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.view.LiveView;
import com.codeborne.selenide.SelenideElement;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.sleep;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BarboraLiveView implements LiveView {
    static BarboraLiveView INSTANCE = new BarboraLiveView();

    private boolean initialized;

    private BarboraLiveView() {}

    @Override
    public void initialize() {
        if (!initialized) {
            declineAllCookies();
            switchToEnglish();
            initialized = true;
        }
    }

    private static void declineAllCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        topMenuItem("Kaubavalik").shouldBe(visible);
        languageSelectDropdown().shouldBe(visible).hover();
        sleep();
        englishLanguageOption().shouldBe(visible).hover();
        sleep();
        englishLanguageOption().shouldBe(visible).click();
        topMenuItem("Products").shouldBe(visible);
    }

    private static SelenideElement topMenuItem(String name) {
        return topMenu().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    private static SelenideElement topMenu() {
        return $("#desktop-menu-placeholder");
    }

    private static SelenideElement englishLanguageOption() {
        return languageSelectDropdown().$$("li").findBy(text("English"));
    }

    private static SelenideElement languageSelectDropdown() {
        return $("#fti-header-language-dropdown");
    }
}