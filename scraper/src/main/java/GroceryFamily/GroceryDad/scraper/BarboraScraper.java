package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTree;
import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
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
        NewBarboraPage.runtime().rootCategoryView().leaves().forEach(leaf -> scrap(leaf, handler));
    }


    private void scrap(NewCategoryView view, Consumer<Product> handler) {
        if (view.isVisited()) return;
        view.markVisited();
        open(view.url);
        waitUntilPageLoads();

        var children = NewBarboraPage.runtime().childCategoryViews(view.codePath);
        if (children.isEmpty()) {
            // todo: scrap products
            System.out.printf("Scraping %s%n", view.namePath());
        } else {
            children.forEach(view::addChild);
            view.leaves().forEach(leaf -> scrap(leaf, handler));
        }

//        var page = NewBarboraPage.runtime();
//        var unseen = page.categoryViewTree().leaves().stream().filter(leaf -> !seen.exists(leaf.path)).toList();
//        if (unseen.isEmpty()) {
//            // todo: scrap products
//        } else {
//            unseen.forEach(leaf -> scrap(leaf, handler, seen));
//        }

//        var unseen = new CategoryViewTree();
//        unseenCategoryViews(seen).forEach(unseenView -> {
//            unseen.add(unseenView);
//            seen.add(unseenView);
//        });
//        if (unseen.isEmpty()) {
//            // todo: scrap products
//        } else {
//            unseen.leaves().forEach(unseenView -> scrap(unseenView, handler, seen));
//        }
    }

//    private List<? extends NewCategoryView> unseenCategoryViews(CategoryViewTree seen) {
//        return categoryViews()
//                .stream()
////                .filter(view -> !seen.exists(view.codePath))
//                .toList();
//    }

//    private List<? extends NewCategoryView> categoryViews() {
//        return Jsoup
//                .parse(html())
//                .select("a[id*=category]")
//                .stream()
//                .map(BarboraCategoryView::new)
////                .filter(view -> categoryAllowed(view.codePath))
//                .toList();
//    }

//    private CategoryViewTree categoryViewTree() {
//        var document = Jsoup.parse(html());
//        var mainCategories = document.select("a[id*=fti-desktop-category]");
//        Jsoup
//                .parse(html())
//                .select("a[id*=category]")
//                .stream()
//                .map(BarboraCategoryView::new)
////                .filter(view -> categoryAllowed(view.codePath))
//                .toList();
//    }


    private void traverse(CategoryView view, Consumer<Product> handler, CategoryTree categories) {
        if (categoryAllowed(view.path)) {
            view.select();
            var children = view.children();
            if (children.isEmpty()) {
                products(view.path).forEach(handler);
                categories.add(view.path);
            } else {
                view.children().forEach(child -> traverse(child, handler, categories));
            }
            view.deselect();
        }
    }

    static List<Product> products(CategoryTreePath path) {
        return productPages(path).stream().flatMap(Collection::stream).toList();
    }

    static List<List<Product>> productPages(CategoryTreePath path) {
        List<List<Product>> pages = new ArrayList<>();
        pages.add(productPage(path));
        while (nextProductPageExists()) {
            nextProductPage();
            pages.add(productPage(path));
        }
        return pages;
    }

    static List<Product> productPage(CategoryTreePath path) {
        return productPageElements()
                .shouldHave(sizeGreaterThan(0))
                .asDynamicIterable()
                .stream()
                .map(BarboraProductView::new)
                .map(view -> view.product(path))
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