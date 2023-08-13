package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.scraper.cache.Cache;
import com.codeborne.selenide.Selenide;
import io.github.antivoland.sfc.FileCache;
import org.jsoup.nodes.Document;

import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.Scraper.waitUntilPageReady;
import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

public abstract class Context {
    private final Cache.Factory cacheFactory;

    public Context(Cache.Factory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public final FileCache<String> cache(Link link) {
        return cacheFactory.html(link.codePath.segments());
    }

    public final String open(Link link) {
        Selenide.open(link.url);
        waitUntilPageReady();
        waitUntilReady();
        return html();
    }

    protected abstract void waitUntilReady();

    public final Stream<Link> childCategoryLinksSortedByCodePathSize(Document document, Link selected) {
        return categoryLinks(document, selected)
                .filter(link -> link.codePath.contains(selected.codePath))
                .filter(link -> !link.codePath.equals(selected.codePath))
                .sorted(comparing(link -> link.codePath.size()));
    }

    protected abstract Stream<Link> categoryLinks(Document document, Link selected);
}