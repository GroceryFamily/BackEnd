package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.Page.textContains;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.apache.commons.lang3.StringUtils.*;

class RimiPage {

    static void category(List<String> categories) {
        if (categories.size() < 2) throw new IllegalArgumentException("Requires at least two categories");

        var button = $("#desktop_category_menu_button").shouldBe(visible);
        button.click();
        int dataLevel = 1;
        for (String category : categories) {
            button = $$("*[data-level='" + dataLevel + "'] li")
                    .shouldHave(sizeGreaterThan(0))
                    .findBy(text(category))
                    .shouldBe(visible);
            button.click();
            ++dataLevel;
        }
    }

    /*
     <li role="none" class="category-list-item gtm js-category-list-item -active">
         <button role="menuitem" class="trigger gtm js-show-descendant-categories" data-target-level="2" data-target-descendant="2" aria-owns="desktop_category_menu_2" type="button">
         <img class="icon" src="https://rimibaltic-web-res.cloudinary.com/image/upload/f_png,q_auto/v1/ecom-cms/a0e50d3b0b6f72bbb7890364809ebf6082fee77c-seYlWfezaS" alt="Talu Toidab - Farmer`s market">
         <span class="name">Talu Toidab - Farmer`s market</span>
         <svg class="arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"><path fill="none" stroke="currentColor" stroke-miterlimit="10" stroke-width="2" d="M16 40l16-16L16 8"></path></svg>                    </button>
    </li>

     <li role="none" class="category-list-item gtm js-category-list-item -active">
         <button role="menuitem" class="trigger gtm js-show-descendant-categories" data-target-level="3" data-target-descendant="2_6" aria-owns="desktop_category_menu_2_6" type="button">
         <span class="name">Talu Toidab sweets, snacks</span>
         <svg class="arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"><path fill="none" stroke="currentColor" stroke-miterlimit="10" stroke-width="2" d="M16 40l16-16L16 8"></path></svg>                    </button>
     </li>

     <li role="none" class="category-list-item gtm">
         <a role="menuitem" class="trigger" href="/epood/en/products/talu-toidab---farmer-s-market/talu-toidab-sweets-snacks/talu-toidab-sweets/c/SH-19-6-1">
             <span class="name">Talu Toidab sweets</span>
         </a>
     </li>
     */
    static List<CategoryView> mainCategoryViews() {
        return mainCategoryElements()
                .asDynamicIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(categoryCode(e))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(new CategoryTreePath(category))
//                        .select(() -> {
//                            mainCategoryElement(category).shouldBe(visible).click();
//                            breadcrumbElement(category).shouldBe(visible);
//                        })
//                        .children(RimiPage::contextCategoryViews)
                        .deselect(() -> {})
                        .build())
                .toList();
    }

    static String categoryCode(SelenideElement mainCategoryElement) {
        var childCategoriesElement = $("#" + mainCategoryElement.attr("aria-owns")).$$("li");
        var mainCategoryUrl = childCategoriesElement.findBy(cssClass("base-category")).attr("href");
        return substringBefore(substringAfter(mainCategoryUrl, "products/"), "/");
    }

//    private static List<CategoryView> contextCategoryViews(CategoryTreePath path) {
//        if (childCategoriesElement().exists()) {
//            return childCategoryElements()
//                    .asFixedIterable()
//                    .stream()
//                    .map(e -> Category
//                            .builder()
//                            .code(substringAfterLast(e.attr("href"), "/"))
//                            .name(e.$("div").text())
//                            .build())
//                    .map(category -> CategoryView
//                            .builder()
//                            .path(path.add(category))
//                            .select(() -> {
//                                childCategoryElement(category).shouldBe(visible).click();
//                                breadcrumbElement(category).shouldBe(visible);
//                            })
//                            .children(BarboraPage::contextCategoryViews)
//                            .deselect(() -> breadcrumbElement(path.last()).shouldBe(visible).click())
//                            .build())
//                    .toList();
//        }

//        return grandchildCategoryElements()
//                .asFixedIterable()
//                .stream()
//                .map(e -> Category
//                        .builder()
//                        .code(substringAfterLast(e.attr("href"), "/"))
//                        .name(substringBeforeLast(e.text(), " "))
//                        .build())
//                .map(category -> CategoryView
//                        .builder()
//                        .path(path.add(category))
//                        .select(() -> {
//                            grandchildCategoryElement(category).shouldBe(visible).click();
//                            breadcrumbElement(category).shouldBe(visible);
//                        })
//                        .children(BarboraPage::contextCategoryViews)
//                        .deselect(() -> breadcrumbElement(path.last()).shouldBe(visible).click())
//                        .build())
//                .toList();
//    }

    private static SelenideElement mainCategoryElement(Category category) {
        return mainCategoryElements().findBy(textContains(category.name));
    }

    private static ElementsCollection mainCategoryElements() {
        return categoriesElement().$$("button");
    }

    private static SelenideElement categoriesElement() {
        return $("nav[data-gtms-event-category=sideMenu]");
    }

}