package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.context.barbora.BarboraView.categoryCode;
import static GroceryFamily.GroceryDad.scraper.context.barbora.BarboraView.categoryCodePath;

@SuperBuilder
public class BarboraCategoryView extends View implements CategoryView {
    @Override
    public List<Link> childCategoryLinks() {
        var selectedCodePath = selected.codePath();
        return categoryItems().filter(e -> {
            var codePath = categoryCodePath(e.absUrl("href"));
            if (!codePath.contains(selectedCodePath)) return false;
            return codePath.size() - selectedCodePath.size() == 1;
        }).map(e -> {
            var url = e.absUrl("href");
            return Link
                    .builder()
                    .code(categoryCode(e.absUrl("href")))
                    .name(e.text().replaceAll("\s[0-9]+$", ""))
                    .url(url)
                    .source(selected)
                    .build();
        }).toList();
    }

    private Stream<Element> categoryItems() {
        return document.select("a[class*=category]").stream().filter(Element::hasText);
    }
}