package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static GroceryFamily.GroceryDad.scraper.BarboraPage.*;
import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

@Slf4j
@SuperBuilder
class BarboraScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        sleep();
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    @Override
    protected void switchToEnglish() {
        sleep();
        languageSelectElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).click();
        topMenuItemElement("Products").shouldBe(visible);
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        /* todo: remove
        var categories = buildCategoryTree();
        log.info("[BARBORA] Categories: {}", categories);
        // todo: scrap
         */

        var categories = new CategoryTree();
        mainCategoryViews().forEach(view -> traverse(view, handler, categories));
        log.info("[BARBORA] Traversed categories: {}", categories);
    }


    private void traverse(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
        if (categoryAllowed(view.path)) {
            view.select();
            var children = view.children();
            if (children.isEmpty()) {
                categories.add(view.path);
            } else {
                view.children().forEach(child -> traverse(child, handler, categories));
            }
//            if (view.isLeaf()) {
                /* todo: scrap products
                products(view.path).forEach(handler);
                 */
//            categories.add(view.path);
//            } else {
//                categoryViews(view).forEach(child -> traverse(child, handler, categories));
//            }
            categories.add(view.path);
            view.deselect();
        }
    }

    /* todo: remove
    private List<CategoryView> rootCategoryViews() {
        return mainCategoryElements()
                .shouldHave(sizeGreaterThan(0))
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
                            rootCategoryElement(category).shouldBe(visible).click();
                            breadcrumbElement(category).shouldBe(visible);
                            sleep();
                        })
                        .leaf(() -> false)
                        .deselect(() -> {})
                        .build())
                .toList();
    }

    private static List<CategoryView> categoryViews(CategoryView parent) {
        return contextCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(parent.path.add(category))
                        .select(() -> categoryElement(category).shouldBe(visible).click())
                        .leaf(() -> false)
                        .deselect(() -> breadcrumbElement(parent.category()).shouldBe(visible).click())
                        .build())
                .toList();
    }
     */

    /* todo: remove
    private static List<CategoryView> childCategoryViews(CategoryView parent) {
        return childCategoryElements()
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.$("div").text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(parent.path.add(category))
                        .select(() -> {
                            parent.e().hover();
                            childCategoryElement(category).click();

                        })
                        .leaf(() -> false)
                        .deselect(() -> {})
                        .e(() -> {
                            parent.e().hover();
                            return childCategoryElement(category);
                        })
                        .build())
                .toList();
    }

    private static List<CategoryView> grandchildCategoryViews(CategoryView grandparent, CategoryView parent) {
        return grandchildCategoryElements(parent.e())
                .shouldHave(sizeGreaterThan(0))
                .asFixedIterable()
                .stream()
                .map(e -> Category
                        .builder()
                        .code(substringAfterLast(e.attr("href"), "/"))
                        .name(e.text())
                        .build())
                .map(category -> CategoryView
                        .builder()
                        .path(parent.path.add(category))
                        .select(() -> {
                            grandparent.e().hover();
                            grandchildCategoryElement(category).click();
                        })
                        .leaf(() -> true)
                        .deselect(() -> {})
                        .e(() -> {
                            grandparent.e().hover();
                            return grandchildCategoryElement(category);
                        })
                        .build())
                .toList();
    }
     */

    @Override
    protected void scrap(List<String> categories, Consumer<Product> handler) {
        category(categories);
        products().forEach(handler);
        // todo: finalize
    }

    static void category(List<String> categories) {
        if (categories.size() < 2) throw new IllegalArgumentException("Requires at least two categories");

        var firstCategory = $$("*[id*='fti-desktop-category']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(0)))
                .shouldBe(visible);
        firstCategory.hover();

        var secondCategory = $$("*[id*='fti-category-tree-child'] > div")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(1)))
                .shouldBe(visible);
        if (categories.size() == 2) {
            secondCategory.click();
            return;
        }

        var thirdCategory = $$("*[id*='fti-category-tree-grand-child']")
                .shouldHave(sizeGreaterThan(0))
                .findBy(text(categories.get(2)))
                .shouldBe(visible);
        thirdCategory.click();

        if (categories.size() > 3) throw new IllegalArgumentException("Requires no more than three categories");
    }

    static Collection<Product> products() {
        Collection<Product> products = new ArrayList<>();
        for (var e : $$("*[itemtype*='Product']").shouldHave(sizeGreaterThan(0))) {
            if (e.$("*[itemprop='price']").exists()) {
                products.add(product(e));
            }
        }
        return products;
    }

    static Product product(SelenideElement e) {
        return Product
                .builder()
                .namespace(Namespace.BARBORA)
                .code(e.$("div").attr("data-b-item-id"))
                .name(e.$("*[itemprop='name']").text())
                .prices(Set.of(
                        pcPrice(e.$("*[itemprop='price']").text()),
                        price(e.$("*[class='b-product-price--extra']").text())))
                .build();
    }

    // €2.29
    static Price pcPrice(String text) {
        return Price
                .builder()
                .unit(PriceUnit.PC)
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(text.substring(1)))
                .build();
    }

    // €1.15/l
    static Price price(String text) {
        var fragments = text.substring(1).split("/");
        return Price
                .builder()
                .unit(PriceUnit.normalize(fragments[1]))
                .currency(currency(text.substring(0, 1)))
                .amount(new BigDecimal(fragments[0]))
                .build();
    }

    static String currency(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Currency symbol is missing");
        if (symbol.equals("€")) return Currency.EUR;
        throw new UnsupportedOperationException(format("Currency symbol '%s' is not recognized", symbol));
    }
}