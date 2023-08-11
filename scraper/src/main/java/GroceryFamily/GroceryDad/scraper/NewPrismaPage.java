package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.tree.CategoryViewTree;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.WebDriverRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

class NewPrismaPage {
    private final Document document;

    NewPrismaPage(String html, String url) {
        this.document = Jsoup.parse(html, url);
    }

    CategoryViewTree categoryViewTree() {
        var tree = new CategoryViewTree();
        topCategoryViews().forEach(tree::add);
        return tree;
    }

    CategoryViewTree subcategoryViewTree(CategoryTreePath path) {
        var tree = new CategoryViewTree();
        leftCategoryViews(path).forEach(tree::add);
        return tree;
    }

    private List<NewCategoryView> topCategoryViews() {
        return document.select("#main-navigation a[href*=selection]")
                .stream()
                .map(e -> {
                    var category = Category
                            .builder()
                            .code(substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/"))
                            .name(e.text())
                            .build();
                    return NewCategoryView
                            .builder()
                            .oldPath(new CategoryTreePath(category))
                            .category(category)
                            .url(e.absUrl("href"))
                            .build();
                })
                .toList();
    }

    private List<NewCategoryView> leftCategoryViews(CategoryTreePath path) {
        return document.select("*[role=navigation] a[data-category-id]")
                .stream()
                .map(e -> {
                    var category = Category
                            .builder()
                            .code(substringAfterLast(e.attr("href"), "/"))
                            .name(e.text())
                            .build();
                    return NewCategoryView
                            .builder()
                            .oldPath(path.add(category))
                            .category(category)
                            .url(e.absUrl("href"))
                            .build();
                })
                .toList();
    }

    public static NewPrismaPage runtime() {
        return new NewPrismaPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}