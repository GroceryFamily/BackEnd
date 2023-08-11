package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.WebDriverRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.substringAfter;

class NewBarboraPage {
    private final Document document;

    NewBarboraPage(String html, String url) {
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

    private Stream<NewCategoryView> childCategoryViewsSortedByCodePath(Path<String> parentCodePath) {
        return categoryLinks()
                .map(link -> {
                    var codePath = categoryCodePath(link);
                    return NewCategoryView
                            .builder()
                            .codePath(codePath)
                            .category(Category
                                    .builder()
                                    .code(codePath.tail())
                                    .name(link.text())
                                    .build())
                            .url(link.absUrl("href"))
                            .build();
                })
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

    public static NewBarboraPage runtime() {
        return new NewBarboraPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}