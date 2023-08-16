package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.context.barbora.BarboraView.categoryCodePath;

@SuperBuilder
class BarboraCategoryView extends View implements CategoryView {
    @Override
    public List<Link> childCategoryLinks() {
        return items()
                .filter(childOfSelected())
                .map(e -> Link
                        .builder()
                        .code(codePath(e).last())
                        .name(e.text().replaceAll("\s[0-9]+$", ""))
                        .url(url(e))
                        .source(selected)
                        .build())
                .toList();
    }

    private Stream<Element> items() {
        return document.select("a[class*=category]").stream().filter(Element::hasText);
    }

    private Predicate<Element> childOfSelected() {
        return codePathFilter(BarboraCategoryView::codePath).childOf(selected.codePath());
    }

    private static Path<String> codePath(Element e) {
        return categoryCodePath(url(e));
    }

    private static String url(Element e) {
        return e.absUrl("href");
    }
}