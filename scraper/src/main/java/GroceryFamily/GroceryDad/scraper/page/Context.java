package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.tree.CategoryPermissionTree;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.Selenide;
import io.github.antivoland.sfc.FileCache;
import org.jsoup.nodes.Document;

import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.Scraper.waitUntilPageReady;
import static GroceryFamily.GroceryDad.scraper.page.Page.html;
import static java.util.Comparator.comparing;

public abstract class Context {
    private final Cache.Factory cacheFactory;
    private final CategoryPermissionTree permissions;

    public Context(Cache.Factory cacheFactory, CategoryPermissionTree permissions) {
        this.cacheFactory = cacheFactory;
        this.permissions = permissions;
    }

    public final boolean canOpen(Path<String> namePath) {
        return permissions.allowed(namePath.segments());
    }

    public final FileCache<String> cache(Path<String> categoryPath) {
        return cacheFactory.html(categoryPath.segments());
    }

    public final FileCache<String> productsCache(Path<String> categoryPath) {
        var path = categoryPath.followedBy("products");
        return cacheFactory.html(path.segments());
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

    public Stream<Product> loadProducts(Path<String> categoryPath, Link selected) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}