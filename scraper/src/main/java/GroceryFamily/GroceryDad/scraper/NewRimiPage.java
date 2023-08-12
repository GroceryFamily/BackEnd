package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.WebDriverRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

class NewRimiPage {
    private final Document document;
    private final Path<String> codePath;

    NewRimiPage(String html, String url, Path<String> codePath) {
        this.document = Jsoup.parse(html, url);
        this.codePath = codePath;
    }

    NewCategoryView rootCategoryView() {
        var root = NewCategoryView.root();
        childCategoryViews(root.codePath).forEach(root::addChild);
        return root;
    }

    List<NewCategoryView> childCategoryViews(Path<String> parentCodePath) {
        var views = new HashMap<Path<String>, NewCategoryView>();
        views.put(parentCodePath, NewCategoryView.root());
        childCategoryLinksSortedByCodePath().forEach(link -> {
            var view = NewCategoryView
                    .builder()
                    .codePath(link.codePath)
                    .category(link.category)
                    .url(link.absoluteURL)
                    .build();
            var parent = views.get(link.codePath.parent());
            parent.addChild(view);
            views.put(link.codePath, view);
        });
        return views.get(parentCodePath).detachChildren();
    }

    Stream<CategoryLink> childCategoryLinksSortedByCodePath() {
        return categoryLinks()
                .filter(view -> view.codePath.contains(codePath))
                .filter(view -> !view.codePath.equals(codePath))
                .sorted(comparing(view -> view.codePath.size()));
    }

    private Stream<CategoryLink> categoryLinks() {
        var buttons = document.select("nav[data-category-menu-container] button");
        return Stream.concat(buttons.stream().map(button -> {
                    var codePath = categoryCodePath(link(button));
                    var category = Category
                            .builder()
                            .code(codePath.tail())
                            .name(button.text())
                            .build();
                    return CategoryLink
                            .builder()
                            .codePath(codePath)
                            .category(category)
                            .relativeURL(link(button).attr("href"))
                            .absoluteURL(link(button).absUrl("href"))
                            .build();
                }), buttons.stream().flatMap(button -> submenuLinks(button).stream().map(link -> {
                    var codePath = categoryCodePath(link);
                    var category = Category
                            .builder()
                            .code(codePath.tail())
                            .name(link.text())
                            .build();
                    return CategoryLink
                            .builder()
                            .codePath(codePath)
                            .category(category)
                            .relativeURL(link.attr("href"))
                            .absoluteURL(link.absUrl("href"))
                            .build();
                }))
        );

    }

    private static Path<String> categoryCodePath(Element e) {
        var fragments = e.attr("href").split("/");
        return Path.of(List.of(fragments).subList(4, fragments.length - 2));
    }

    private Element link(Element button) {
        return submenu(button).select("a[class*=base-category-link]").first();
    }

    private Elements submenuLinks(Element button) {
        return submenu(button).select("a").not("[class*=base-category-link]");
    }

    private Element submenu(Element button) {
        return document.select("*[data-index=" + button.attr("data-target-descendant") + "]").first();
    }

    public static NewRimiPage runtime(Path<String> codePath) {
        return new NewRimiPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl(), codePath);
    }
}