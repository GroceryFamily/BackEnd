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
    static List<CategoryView> mainCategoryViews() {
        return mainCategoryElements()
                .asDynamicIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(new CategoryTreePath(category))
                        .select(() -> {
                            mainCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                        })
                        .children(BarboraPage::contextCategoryViews)
                        .deselect(() -> {})
                        .build())
                .toList();
    }

    private static List<CategoryView> contextCategoryViews(CategoryTreePath path) {
        if (childCategoriesElement().exists()) {
            return childCategoryElements()
                    .asFixedIterable()
                    .stream()
                    .map(e -> Category
                            .builder()
                            .code(substringAfterLast(e.attr("href"), "/"))
                            .name(e.$("div").text())
                            .build())
                    .map(category -> CategoryView
                            .builder()
                            .path(path.add(category))
                            .select(() -> {
                                childCategoryElement(category).shouldBe(visible).click();
                                breadcrumbElement(category).shouldBe(visible);
                            })
                            .children(BarboraPage::contextCategoryViews)
                            .deselect(() -> breadcrumbElement(path.last()).shouldBe(visible).click())
                            .build())
                    .toList();
        }

        return grandchildCategoryElements()
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(substringBeforeLast(e.text(), " "))
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(path.add(category))
                        .select(() -> {
                            grandchildCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                        })
                        .children(BarboraPage::contextCategoryViews)
                        .deselect(() -> breadcrumbElement(path.last()).shouldBe(visible).click())
                        .build())
                .toList();
    }

    private static SelenideElement mainCategoryElement(Category category) {
        return mainCategoryElements().findBy(hrefContains(category.code));
    }

    private static ElementsCollection mainCategoryElements() {
        return topMenuElement().shouldBe(visible).$$("a[id*=fti-desktop-category]");
    }

    private static SelenideElement childCategoryElement(Category category) {
        return childCategoryElements().findBy(hrefContains(category.code)).$("div");
    }

    private static ElementsCollection childCategoryElements() {
        return childCategoriesElement().shouldBe(visible).$$("a[id*=fti-category-tree-child]");
    }

    private static SelenideElement childCategoriesElement() {
        return $("#category-tree-placeholder");
    }

    private static SelenideElement grandchildCategoryElement(Category category) {
        return grandchildCategoryElements().findBy(hrefContains(category.code));
    }

    private static ElementsCollection grandchildCategoryElements() {
        var selectedCategoryName = breadcrumbElements().last().text();
        return grandchildCategoriesElement().$$("a").filter(not(textContains(selectedCategoryName)));
    }

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