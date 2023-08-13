package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.Page.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

class BarboraPage {
    private static SelenideElement grandchildCategoriesElement() {
        return $("ul[class*=category-children]");
    }

    static SelenideElement breadcrumbElement(Category category) {
        return breadcrumbElements().findBy(hrefContains(category.code));
    }

    private static ElementsCollection breadcrumbElements() {
        return $$("ol[class=breadcrumb] a");
    }

    static SelenideElement topMenuItemElement(String name) {
        return topMenuElement().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    private static SelenideElement topMenuElement() {
        return $("#desktop-menu-placeholder");
    }

    static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }

    static ElementsCollection productPageElements() {
        return productPageElement().$$("*[itemtype*=Product]");
    }

    static SelenideElement productPageElement() {
        return $("*[class*=products-list]");
    }

    static boolean nextProductPageExists() {
        return selectedProductPageNumber() < lastProductPageNumber();
    }

    static void nextProductPage() {
        var nextPageNumber = selectedProductPageNumber() + 1;
        productPageNumberElement(nextPageNumber).$("a").click();
        productPageNumberElement(nextPageNumber).shouldHave(cssClass("active"));
    }

    static SelenideElement productPageNumberElement(int pageNumber) {
        return productPageNumberElements().findBy(number(pageNumber));
    }

    static int lastProductPageNumber() {
        return Integer.parseInt(productPageNumberElements().last().text());
    }

    static int selectedProductPageNumber() {
        return Integer.parseInt(productPageNumberElements().findBy(cssClass("active")).text());
    }

    static ElementsCollection productPageNumberElements() {
        return $("ul[class=pagination]").$$("li").filter(number());
    }
}