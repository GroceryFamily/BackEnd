package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static GroceryFamily.GroceryDad.scraper.page.Page.hrefContains;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

class BarboraPage {
    static SelenideElement categoryElement(Category category) {
        return categoryElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection categoryElements() {
        return $$("*[id*='fti-desktop-category']");
    }

    static SelenideElement childCategoryElement(Category category) {
        return childCategoryElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection childCategoryElements() {
        return $$("*[id*='fti-category-tree-child']");
    }

    static SelenideElement grandchildCategoryElement(Category category) {
        return grandchildCategoryElements().findBy(hrefContains(category.code));
    }

    private static ElementsCollection grandchildCategoryElements() {
        return $$("*[id*='fti-category-tree-grand-child']");
    }

    static ElementsCollection grandchildCategoryElements(SelenideElement e) {
        return e.$$("*[id*='fti-category-tree-grand-child']");
    }

    // ************************************************************************

    static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }

    static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    static SelenideElement topMenuItemElement(String name) {
        return topMenuElement().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    static SelenideElement topMenuElement() {
        return $("#desktop-menu-placeholder");
    }

    static SelenideElement rootCategoryElement(Category category) {
        return rootCategoryElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection rootCategoryElements() {
        return topMenuElement().$$("a[id*=fti-desktop-category]");
    }

    static SelenideElement breadcrumbElement(Category category) {
        return breadcrumbElements().findBy(hrefContains(category.code));
    }

    static ElementsCollection breadcrumbElements() {
        return $$("ol[class=breadcrumb] a");
    }
}