package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static GroceryFamily.GroceryDad.scraper.page.Page.*;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

class PrismaPage {
    static SelenideElement topCategoryElement(Category category) {
        return topCategoryElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection topCategoryElements() {
        return $$("*[id='main-navigation'] a[href*='selection']");
    }

    static SelenideElement leftCategoryElement(Category category) {
        return leftCategoryElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection leftCategoryElements() {
        return $$("*[role='navigation'] a[data-category-id]");
    }

    static SelenideElement breadcrumbElement(Category category) {
        return breadcrumbElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection breadcrumbElements() {
        return $$("*[class='breadcrumb-item'] *[class='name'] a");
    }

    static ElementsCollection productElements() {
        var totalCount = Integer.parseInt(productCountElement().shouldBe(visible).text());
        var count = visibleProductElementCount();
        while (count < totalCount) {
            scrollDown();
            visibleProductElements().shouldHave(sizeGreaterThan(count));
            count = visibleProductElementCount();
            sleep();
        }
        return visibleProductElements();
    }

    private static SelenideElement productCountElement() {
        return $("*[class*='category-items'] b");
    }

    private static int visibleProductElementCount() {
        return (int) visibleProductElements().asFixedIterable().stream().count();
    }

    private static ElementsCollection visibleProductElements() {
        return $$("li[data-ean]");
    }
}