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
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

class NewPrismaPage {
    private final Document document;
    private final Path<String> codePath;

    NewPrismaPage(String html, String url, Path<String> codePath) {
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

    private Stream<CategoryLink> childCategoryLinksSortedByCodePath() {
        return categoryLinks()
                .filter(view -> view.codePath.contains(codePath))
                .filter(view -> !view.codePath.equals(codePath))
                .sorted(comparing(view -> view.codePath.size()));
    }

    private Stream<CategoryLink> categoryLinks() {
        return Stream.concat(topCategoryLinks(), leftCategoryLinks());
    }

    private Stream<CategoryLink> topCategoryLinks() {
        return document
                .select("#main-navigation a[href*=selection]")
                .stream()
                .filter(Element::hasText)
                .map(NewPrismaPage::topCategoryLink);
    }

    private static CategoryLink topCategoryLink(Element e) {
        var relativeURL = e.attr("href");
        var code = substringAfterLast(substringBeforeLast(relativeURL, "/"), "/");
        var category = Category
                .builder()
                .code(code)
                .name(e.text())
                .build();
        return CategoryLink
                .builder()
                .codePath(Path.<String>empty().followedBy(code))
                .category(category)
                .relativeURL(relativeURL)
                .absoluteURL(e.absUrl("href"))
                .build();
    }

    private Stream<CategoryLink> leftCategoryLinks() {
        return document
                .select("*[role=navigation] a[data-category-id]")
                .stream()
                .filter(Element::hasText)
                .map(this::leftCategoryLink);
    }

    private CategoryLink leftCategoryLink(Element e) {
        var relativeURL = e.attr("href");
        var code = substringAfterLast(relativeURL, "/");
        var category = Category
                .builder()
                .code(code)
                .name(e.text())
                .build();
        return CategoryLink
                .builder()
                .codePath(codePath.followedBy(code))
                .category(category)
                .relativeURL(relativeURL)
                .absoluteURL(e.absUrl("href"))
                .build();
    }

    public static NewPrismaPage runtime(Path<String> codePath) {
        return new NewPrismaPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl(), codePath);
    }
}