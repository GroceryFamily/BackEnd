package GroceryFamily.GroceryDad.scraper.view.rimi;

import GroceryFamily.GroceryDad.scraper.model.Link;
import GroceryFamily.GroceryDad.scraper.model.Path;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.view.rimi.RimiView.categoryCodePath;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@SuperBuilder
class RimiCategoryView extends View implements CategoryView {
    @Override
    public List<Link> childCategoryLinks() {
        return leftMenuItems()
                .filter(childOfSelected())
                .map(e -> Link
                        .builder()
                        .code(codePath(e).last())
                        .name(e.text())
                        .url(url(e))
                        .source(selected)
                        .build())
                .toList();
    }

    private Stream<Element> leftMenuItems() {
        return new LeftMenu().items();
    }

    private class LeftMenu {
        final Map<String, Element> blocks = leftMenuBlocks().collect(toMap(e -> e.attr("data-index"), identity()));

        Stream<Element> items() {
            return Stream.concat(buttons().map(button -> link(button).text(button.text())), links());
        }

        Stream<Element> buttons() {
            return blocks.values().stream().flatMap(block -> block.select("button").stream());
        }

        Element link(Element button) {
            return blocks.get(button.attr("data-target-descendant")).select("a[class*=base]").first();
        }

        Stream<Element> links() {
            return blocks.values().stream().flatMap(block -> block.select("a:not([class*=base])").stream());
        }
    }

    private Stream<Element> leftMenuBlocks() {
        return leftMenu().stream().flatMap(e -> e.select("div").stream());
    }

    private Optional<Element> leftMenu() {
        return Optional.ofNullable(document.select("nav[data-category-menu-container]").first());
    }

    private Predicate<Element> childOfSelected() {
        return codePathFilter(RimiCategoryView::codePath).childOf(selected.codePath());
    }

    private static Path<String> codePath(Element e) {
        return categoryCodePath(url(e));
    }

    private static String url(Element e) {
        return e.absUrl("href");
    }
}