package GroceryFamily.GroceryDad.scraper.worker;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.cache.CacheFactory;
import GroceryFamily.GroceryDad.scraper.model.*;
import GroceryFamily.GroceryDad.scraper.view.ViewFactory;
import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static java.lang.String.format;

@Builder
public class Worker {
    private final String platform;
    private final GroceryDadConfig.Platform config;
    private final CacheFactory cacheFactory;
    private final ViewFactory viewFactory;
    private final Allowlist allowlist;
    private final Listener listener;

    public SourceTree traverse() {
        var state = new WorkerState(config.live);
        try {
            traverse(Link.builder().code(config.namespace).url(config.url).build(), state);
            return state.visited;
        } finally {
            state.destroy();
        }
    }

    private void traverse(Link selected, WorkerState state) {
        if (state.seen.contains(selected.codePath())) return;
        state.seen.add(selected.codePath());
        if (!allowlist.allowed(selected.namePath())) return;

        var document = load(selected, state); // todo: flexible delays based on a platform response latency

        if (selected.source == null) {
            handleCategory(document, Source.category(selected), state);
            return;
        }

        if (selected.source.type == SourceType.PRODUCT_LIST) {
            handleProduct(document, Source.product(selected), state);
            return;
        }

        if (selected.source.type == SourceType.CATEGORY) {
            if (handleCategory(document, Source.category(selected), state)) return;
            handleProductList(document, Source.productList(selected), state);
            return;
        }

        throw new UnsupportedOperationException(format("Source type %s is not supported", selected.source));
    }

    private boolean handleCategory(Document document, Source selected, WorkerState state) {
        var categoryView = viewFactory.categoryView(document, selected);
        var childCategoryLinks = categoryView.childCategoryLinks();
        if (childCategoryLinks.isEmpty()) return false;
        childCategoryLinks.forEach(link -> traverse(link, state));
        state.visited.put(selected.namePath(), selected);
        return true;
    }

    private void handleProductList(Document document, Source selected, WorkerState state) {
        var productListView = viewFactory.productListView(document, selected);
        productListView.productLinks().forEach(link -> traverse(link, state));
        productListView.productPageLinks().forEach(link -> traverse(link, state));
        state.visited.put(selected.namePath(), selected);
    }

    private void handleProduct(Document document, Source selected, WorkerState state) {
        var productView = viewFactory.productView(document, selected);
        listener.product(platform, productView.product(), selected);
        state.visited.put(selected.namePath(), selected);
    }

    private Document load(Link link, WorkerState state) {
        var cache = cacheFactory.html(platform, link);
        var html = cache.load(link.code);
        if (html == null) {
            html = viewFactory.liveView(state.driver()).open(link);
            cache.save(link.code, html);
        }
        return Jsoup.parse(html, link.url);
    }
}