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
import static org.apache.commons.lang3.StringUtils.*;

class NewPrismaPage {
    private final Document document;

    NewPrismaPage(String html, String url) {
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
        return Stream.concat(topCategoryViews(), leftCategoryViews(parentCodePath))
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
//
//    CategoryViewTree categoryViewTree() {
//        var tree = new CategoryViewTree();
//        topCategoryViews().forEach(tree::add);
//        return tree;
//    }
//
//    CategoryViewTree subcategoryViewTree(CategoryTreePath path) {
//        var tree = new CategoryViewTree();
//        leftCategoryViews(path).forEach(tree::add);
//        return tree;
//    }

    private Stream<NewCategoryView> topCategoryViews() {
        var rootCodePath = Path.<String>empty();
        return document.select("#main-navigation a[href*=selection]")
                .stream()
                .map(link -> {
                    var category = Category
                            .builder()
                            .code(substringAfterLast(substringBeforeLast(link.attr("href"), "/"), "/"))
                            .name(link.text())
                            .build();
                    return NewCategoryView
                            .builder()
//                            .oldPath(new CategoryTreePath(category))
                            .codePath(rootCodePath.followedBy(category.code))
                            .category(category)
                            .url(link.absUrl("href"))
                            .build();
                });
    }

    private Stream<NewCategoryView> leftCategoryViews(Path<String> parentCodePath) {
        return document.select("*[role=navigation] a[data-category-id]")
                .stream()
                .map(link -> {
                    var category = Category
                            .builder()
                            .code(substringAfterLast(link.attr("href"), "/"))
                            .name(link.text())
                            .build();
                    return NewCategoryView
                            .builder()
//                            .oldPath(path.add(category))
                            .codePath(parentCodePath.followedBy(category.code))
                            .category(category)
                            .url(link.absUrl("href"))
                            .build();
                });
    }

    public static NewPrismaPage runtime() {
        return new NewPrismaPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}