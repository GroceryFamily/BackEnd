package GroceryFamily.GroceryDad.scraper.view.prisma;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.view.LiveView;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.scrollDown;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;

@SuperBuilder
class PrismaLiveView extends LiveView {
    private static boolean initialized;

    @Override
    public void initialize(Link link) {
        if (!initialized) {
            closeCookieNotice();
            switchToEnglish();
            initialized = true;
        }
        if (leftMenuItems().isEmpty() && !visibleProductListItems().isEmpty()) {
            var count = visibleProductListItems().size();
            while (count < productListSize()) {
                scrollDown();
                visibleProductListItems().shouldHave(sizeGreaterThan(count));
                count = visibleProductListItems().size();
            }
        }
    }

    private void closeCookieNotice() {
        driver.$("*[class*=cookie-notice] *[class=close-icon]").shouldBe(visible).click();
    }

    private void switchToEnglish() {
        driver.$("*[data-language=en]").shouldBe(visible).click();
        topMenuItems().shouldHave(itemWithText("Groceries"));
    }

    // todo: static constants with selectors?
    private ElementsCollection topMenuItems() {
        return driver.$$("#main-navigation a[href*='/selection']");
    }

    private ElementsCollection leftMenuItems() {
        return driver.$$("*[role=navigation] a[data-category-id]");
    }

    private ElementsCollection visibleProductListItems() {
        return driver.$$("li[data-ean]");
    }

    private int productListSize() {
        return Integer.parseInt(productListSizeElement().text());
    }

    private SelenideElement productListSizeElement() {
        return driver.$("*[class*=category-items] b");
    }
}