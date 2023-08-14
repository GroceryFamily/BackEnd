package GroceryFamily.GroceryDad.scraper.page.view;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryDad.scraper.page.SourceType;
import GroceryFamily.GroceryElders.domain.Category;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CategoryView extends View {
    public CategoryView(Document document, Link selected) {
        super(document, selected);
    }

    public final List<Link> childCategoryLinks() {
        var links = new ArrayList<Link>();
        var sourceCodePath = selected.sourceCodePath();
        var sources = new HashMap<Path<String>, Source>();
        sources.put(sourceCodePath, selected.source);
        categories().forEach((codePath, category) -> {
            if (!codePath.contains(sourceCodePath)) return;
            if (codePath.equals(sourceCodePath)) return;
            var source = sources.get(codePath.parent());
            links.add(Link
                    .builder()
                    .code(category.code)
                    .name(category.name)
                    .url(category.url)
                    .source(source)
                    .build());
            sources.put(codePath, Source
                    .builder()
                    .type(SourceType.CATEGORY)
                    .code(category.code)
                    .name(category.name)
                    .parent(source)
                    .build());
        });
        return links;
    }

    protected abstract Map<Path<String>, Category> categories();
}