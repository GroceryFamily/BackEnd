package GroceryFamily.GroceryDad.scraper.context.prisma;

import GroceryFamily.GroceryDad.scraper.view.LiveView;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.scrollDown;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PrismaLiveView implements LiveView {
    static PrismaLiveView INSTANCE = new PrismaLiveView();

    private boolean initialized;

    private PrismaLiveView() {}

    @Override
    public void initialize() {
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

    private static void closeCookieNotice() {
        $("*[class*=cookie-notice] *[class=close-icon]").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        $("*[data-language=en]").shouldBe(visible).click();
        topMenuItems().shouldHave(itemWithText("Groceries"));
    }

    private static ElementsCollection topMenuItems() {
        return $$("#main-navigation a[href*=/selection]");
    }

    private static ElementsCollection leftMenuItems() {
        return $$("*[role=navigation] a[data-category-id]");
    }

    private static ElementsCollection visibleProductListItems() {
        return $$("li[data-ean]");
    }

    private static int productListSize() {
        return Integer.parseInt(productListSizeElement().text());
    }

    private static SelenideElement productListSizeElement() {
        return $("*[class*=category-items] b");
    }
}