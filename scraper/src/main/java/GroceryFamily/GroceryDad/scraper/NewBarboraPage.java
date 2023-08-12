package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;
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
        childCategoryLinksSortedByCodePath(parentCodePath).forEach(link -> {
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

    private Stream<CategoryLink> childCategoryLinksSortedByCodePath(Path<String> parentCodePath) {
        return categoryLinks()
                .filter(view -> view.codePath.contains(parentCodePath))
                .filter(view -> !view.codePath.equals(parentCodePath))
                .sorted(comparing(view -> view.codePath.size()));
    }

    private Stream<CategoryLink> categoryLinks() {
        return document
                .select("a[class*=category]")
                .stream()
                .filter(Element::hasText)
                .map(NewBarboraPage::categoryLink);
    }

    private static CategoryLink categoryLink(Element e) {
        var relativeURL = e.attr("href");
        var codePath = Path.of(substringAfter(relativeURL, "/").split("/"));
        var category = Category
                .builder()
                .code(codePath.tail())
                .name(e.text())
                .build();
        return CategoryLink
                .builder()
                .codePath(codePath)
                .category(category)
                .relativeURL(relativeURL)
                .absoluteURL(e.absUrl("href"))
                .build();
    }

    private static Path<String> categoryCodePath(Element e) {
        var relativeURL = e.attr("href");
        return Path.of(substringAfter(relativeURL, "/").split("/"));
    }

    public static NewBarboraPage runtime() {
        return new NewBarboraPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}