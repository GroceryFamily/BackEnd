package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.tree.CategoryViewTree;
import com.codeborne.selenide.WebDriverRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

class NewBarboraPage {
    private final Document document;

    NewBarboraPage(String html, String url) {
        this.document = Jsoup.parse(html, url);
    }

    CategoryViewTree categoryViewTree() {
        var tree = new CategoryViewTree();
//        document.select("a[class*=category]")
//                .stream()
//                .filter(Element::hasText)
//                .map(BarboraCategoryView::new)
//                .forEach(view -> tree.add(view.codePath, view));
        return tree;
    }

    CategoryViewTree subcategoryViewTree(CategoryTreePath selected) {
        var tree = new CategoryViewTree();
//        document.select("a[class*=category]")
//                .stream()
//                .filter(Element::hasText)
//                .map(BarboraCategoryView::new)
//                .filter(view -> view.codePath.size() > selected.size())
//                .sorted(comparing(view -> view.codePath.size()))
//                .forEach(view -> tree.add(view.codePath, view));
        return tree;
    }

    public static NewBarboraPage runtime() {
        return new NewBarboraPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl());
    }
}