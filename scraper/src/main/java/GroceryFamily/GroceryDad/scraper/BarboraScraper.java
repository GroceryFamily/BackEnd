package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.Node;
import GroceryFamily.GroceryDad.scraper.page.context.BarboraContext;
import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Currency;
import GroceryFamily.GroceryElders.domain.*;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

import static GroceryFamily.GroceryDad.scraper.BarboraPage.*;
import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.String.format;

@Slf4j
@SuperBuilder
class BarboraScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    @Override
    protected void switchToEnglish() {
        topMenuItemElement("Kaubavalik").shouldBe(visible);
        languageSelectElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).click();
        topMenuItemElement("Products").shouldBe(visible);
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        Node.root(rootURL(), new BarboraContext(cacheFactory(), categoryPermissions)).traverse(handler);
    }

    private void scrap(CategoryLink link, Consumer<Product> handler, Set<String> visited) {
//        if (visited.contains(link.url)) return;
//        // if (!categoryAllowed(view.namePath())) return; // todo: move on
//        visited.add(link.url);
//
//        open(link.url);
//        waitUntilPageReady();
//        link.initialize();
//
//        var children = NewBarboraPage
//                .builder()
//                .
//                .build()
//                .childCategoryLinkTree();
//
//
//        var children = NewBarboraPage.runtime(view.codePath).childCategoryViews(view.codePath);
//        if (children.isEmpty()) {
//            // products(view).forEach(handler); // todo: fix
//            System.out.printf("Scraping %s%n", view.namePath());
//        } else {
//            children.forEach(view::addChild);
//            view.leaves().forEach(leaf -> scrap(leaf, handler));
//        }
    }

    private void scrap(NewCategoryView view, Consumer<Product> handler) {
//        if (view.isVisited()) return;
//        // if (!categoryAllowed(view.namePath())) return; // todo: move on
//        view.markVisited();
//        open(view.url);
//        waitUntilPageReady();
//
//        var children = NewBarboraPage.runtime(view.codePath).childCategoryViews(view.codePath);
//        if (children.isEmpty()) {
//            // products(view).forEach(handler); // todo: fix
//            System.out.printf("Scraping %s%n", view.namePath());
//        } else {
//            children.forEach(view::addChild);
//            view.leaves().forEach(leaf -> scrap(leaf, handler));
//        }
    }

    //
//    private void traverse(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
//        if (categoryAllowed(view)) {
//            view.select();
//            var children = view.children();
//            if (children.isEmpty()) {
//                products(view).forEach(handler);
//                categories.add(view.path);
//            } else {
//                view.children().forEach(child -> traverse(child, handler, categories));
//            }
//            view.deselect();
//        }
//    }
//
    static List<Product> products(NewCategoryView categoryView) {
        return productPages(categoryView).stream().flatMap(Collection::stream).toList();
    }

    static List<List<Product>> productPages(NewCategoryView categoryView) {
        List<List<Product>> pages = new ArrayList<>();
        pages.add(productPage(categoryView));
        while (nextProductPageExists()) {
            nextProductPage();
            pages.add(productPage(categoryView));
        }
        return pages;
    }

    static List<Product> productPage(NewCategoryView categoryView) {
        return productPageElements()
                .shouldHave(sizeGreaterThan(0))
                .asDynamicIterable()
                .stream()
                .map(BarboraProductView::new)
                .map(view -> view.product(categoryView.categories()))
                .toList();
//
//
//        boolean nextPageExists;
//        do {
//            nextPageExists = nextProductPageExists();
//
//        } while (nextPageExists);
//
//        Collection<Product> products = new ArrayList<>();
//        for (var e : $$("*[itemtype*='Product']").shouldHave(sizeGreaterThan(0))) {
//            if (e.$("*[itemprop='price']").exists()) {
//                products.add(product(e));
//            }
//        }
//        return products;
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