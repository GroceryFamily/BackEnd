package GroceryFamily.GroceryDad.scraper.page;

import com.codeborne.selenide.Selenide;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.Scraper.waitUntilPageReady;
import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

public abstract class Context {
    private Link selected;
    private Document document;

    public final void select(Link link) {
        Selenide.open(link.url);
        waitUntilPageReady();
        waitUntilReady();
        selected = link;
        document = Jsoup.parse(html(), link.url);
    }

    protected abstract void waitUntilReady();

    public final Stream<Link> childCategoryLinksSortedByCodePathSize() {
        return categoryLinks(document, selected)
                .filter(link -> link.codePath.contains(selected.codePath))
                .filter(link -> !link.codePath.equals(selected.codePath))
                .sorted(comparing(link -> link.codePath.size()));
    }

    protected abstract Stream<Link> categoryLinks(Document document, Link selected);
}