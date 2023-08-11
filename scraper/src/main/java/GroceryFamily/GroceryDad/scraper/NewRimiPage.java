package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.tree.CategoryViewTree;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import com.codeborne.selenide.WebDriverRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

class NewRimiPage {
    private final Document document;

    NewRimiPage(String html, String url) {
        this.document = Jsoup.parse(html, url);
    }

    CategoryViewTree categoryViewTree() {
        var buttons = document
                .select("nav[data-category-menu-container] button")
                .stream()
                .sorted(comparing(e -> e.attr("data-target-level")))
                .toList();
        var categories = new HashMap<List<String>, Category>();
        var urls = new HashMap<List<String>, String>();
        buttons.forEach(button -> {
            var codePath = codePath(link(button));
            categories.put(codePath, Category
                    .builder()
                    .code(codePath.get(codePath.size() - 1))
                    .name(button.text())
                    .build());
            urls.put(codePath, link(button).absUrl("href"));
            submenuLinks(button).forEach(link -> {
                var childCodePath = codePath(link);
                categories.put(childCodePath, Category
                        .builder()
                        .code(childCodePath.get(childCodePath.size() - 1))
                        .name(link.text())
                        .build());
                urls.put(childCodePath, link.absUrl("href"));
            });
        });
        var tree = new CategoryViewTree();
        categories.forEach((codePath, category) -> {
            var path = new CategoryTreePath(categories.get(codePath.subList(0, 1)));
            for (int l = 2; l <= codePath.size(); ++l) {
                category = categories.get(codePath.subList(0, l));
                path = path.add(category);
            }
            var view = NewCategoryView
                    .builder()
                    .oldPath(path)
                    .category(category)
                    .url(urls.get(codePath))
                    .build();
            tree.add(view);
        });
        return tree;
    }

    private static List<String> codePath(Element e) {
        var fragments = e.attr("href").split("/");
        return List.of(fragments).subList(4, fragments.length - 2);
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