package GroceryFamily.GroceryDad.scraper.context.rimi;

import GroceryFamily.GroceryDad.scraper.view.LiveView;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

class RimiLiveView implements LiveView {
    static RimiLiveView INSTANCE = new RimiLiveView();

    private boolean initialized;

    private RimiLiveView() {}

    @Override
    public void initialize() {
        if (!initialized) {
            declineAllCookies();
            initialized = true;
        }
    }

    private static void declineAllCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }
}