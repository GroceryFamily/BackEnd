package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static GroceryFamily.GroceryDad.scraper.page.Page.hrefContains;
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
}