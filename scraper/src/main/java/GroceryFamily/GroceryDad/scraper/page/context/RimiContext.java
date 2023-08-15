package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Category;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GroceryElders.domain.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.*;

public class RimiContext extends Context {
    private boolean initialized;

    public RimiContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            initialized = true;
        }
    }

    @Override
    protected Map<Path<String>, Category> categories(Document document, Source selected) {
        var categories = new HashMap<Path<String>, Category>();
        var buttons = categoryButtonElements(document).toList();
        buttons.forEach(be -> {
            {
                var le = categoryLinkElement(document, be);
                var codePath = categoryCodePath(le);
                categories.put(codePath, Category
                        .builder()
                        .code(codePath.tail())
                        .name(be.text())
                        .url(categoryLinkElement(document, be).absUrl("href"))
                        .build());
            }

            subcategoryLinkElements(document, be).forEach(le -> {
                var codePath = categoryCodePath(le);
                categories.put(codePath, Category
                        .builder()
                        .code(codePath.tail())
                        .name(le.text())
                        .url(le.absUrl("href"))
                        .build());
            });
        });
        return categories;
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return productPageNumberElementsExcludingSelected(document)
                .map(e -> {
                    var number = e.text();
                    var codeSuffix = "1".equals(number) ? "" : "@" + e.text();
                    return Link
                            .builder()
                            .code(substringBefore(selected.code, "@") + codeSuffix)
                            .name(selected.name)
                            .url(e.absUrl("href"))
                            .source(selected.parent)
                            .build();
                })
                .toList();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return productListElements(document)
                .map(e -> {
                    var url = requireNonNull(e.select("a").first()).absUrl("href");
                    return Link
                            .builder()
                            .code(productCode(url))
                            .name(e.select("*[class*=name]").text())
                            .url(url)
                            .source(selected)
                            .build();
                })
                .toList();
    }

    @Override
    public Product product(Document document, Source selected) {
        return Product
                .builder()
                .namespace(Namespace.RIMI)
                .code(productCode(selected.url))
                .name(document.select("h3[class=name]").text())
                .url(selected.url)
                // todo: set prices and categories
                .build();
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }

    private static Stream<Element> categoryButtonElements(Document document) {
        return document.select("nav[data-category-menu-container] button").stream();
    }

    private Element categoryLinkElement(Document document, Element be) {
        return subcategoriesElement(document, be).select("a[class*=base]").first();
    }

    private Stream<Element> subcategoryLinkElements(Document document, Element be) {
        return subcategoriesElement(document, be).select("a:not([class*=base])").stream();
    }

    private static Element subcategoriesElement(Document document, Element be) {
        return document.select("*[data-index=" + be.attr("data-target-descendant") + "]").first();
    }

    private static Path<String> categoryCodePath(Element e) {
        var fragments = e.attr("href").split("/");
        return Path.of(List.of(fragments).subList(4, fragments.length - 2));
    }

    private static Stream<Element> productPageNumberElementsExcludingSelected(Document document) {
        return document.select("ul[class*=pagination] a:matches([0-9]+)").stream();
    }

    private static Stream<Element> productListElements(Document document) {
        return document.select("*[data-product-code]").stream();
    }

    private static String productCode(String url) {
        return substringAfterLast(substringBeforeLast(url, "/p/"), "/");
    }
}