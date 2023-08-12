package GroceryFamily.GroceryDad.scraper;

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
import static org.apache.commons.lang3.StringUtils.substringAfter;

class NewRimiPage {
    private final Document document;

    NewRimiPage(String html, String url) {
        this.document = Jsoup.parse(html, url);
    }

    NewCategoryView rootCategoryView() {
        var root = NewCategoryView.root();
        childCategoryViews(root.codePath).forEach(root::addChild);
        return root;
    }

    List<NewCategoryView> childCategoryViews(Path<String> parentCodePath) {
        var views = new HashMap<Path<String>, NewCategoryView>();
        views.put(parentCodePath, NewCategoryView.root());
        childCategoryViewsSortedByCodePath(parentCodePath).forEach(view -> {
            var parent = views.get(view.codePath.parent());
            parent.addChild(view);
            views.put(view.codePath, view);
        });
        return views.get(parentCodePath).detachChildren();
    }

    Stream<NewCategoryView> childCategoryViewsSortedByCodePath(Path<String> parentCodePath) {
        var buttons = document.select("nav[data-category-menu-container] button");
        return Stream.concat(buttons.stream().map(button -> {
                            var codePath = codePath(link(button));
                            var category = Category
                                    .builder()
                                    .code(codePath.tail())
                                    .name(button.text())
                                    .build();
                            return NewCategoryView
                                    .builder()
                                    .codePath(codePath)
                                    .category(category)
                                    .url(link(button).absUrl("href"))
                                    .build();
                        }), buttons.stream().flatMap(button -> submenuLinks(button).stream().map(link -> {
                            var codePath = codePath(link);
                            var category = Category
                                    .builder()
                                    .code(codePath.tail())
                                    .name(link.text())
                                    .build();
                            return NewCategoryView
                                    .builder()
                                    .codePath(codePath)
                                    .category(category)
                                    .url(link.absUrl("href"))
                                    .build();
                        }))
                )
                .filter(view -> view.codePath.contains(parentCodePath))
                .filter(view -> !view.codePath.equals(parentCodePath))
                .sorted(comparing(view -> view.codePath.size()));
    }

    private Stream<Element> categoryLinks() {
        return document.select("a[class*=category]").stream().filter(Element::hasText);
    }

    private static Path<String> categoryCodePath(Element categoryLink) {
        var url = categoryLink.attr("href");
        return Path.of(substringAfter(url, "/").split("/"));
    }

    private static Path<String> codePath(Element e) {
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

    public static NewRimiPage runtime() {
        return new NewRimiPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}