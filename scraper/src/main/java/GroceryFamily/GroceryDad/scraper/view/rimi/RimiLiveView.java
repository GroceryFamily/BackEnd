package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.view.LiveView;
import lombok.experimental.SuperBuilder;

import static com.codeborne.selenide.Condition.visible;

@SuperBuilder
class RimiLiveView extends LiveView {
    private static boolean initialized;

    @Override
    public void initialize(Link link) {
        if (!initialized) {
            declineAllCookies();
            initialized = true;
        }
    }

    private void declineAllCookies() {
        driver.$("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }
}