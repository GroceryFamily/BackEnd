package GroceryFamily.GroceryDad.scraper.context.rimi;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.context.rimi.RimiView.categoryCode;
import static GroceryFamily.GroceryDad.scraper.context.rimi.RimiView.categoryCodePath;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@SuperBuilder
public class RimiCategoryView extends View implements CategoryView {
    @Override
    public List<Link> childCategoryLinks() {
        var selectedCodePath = selected.codePath();
        return new LeftMenu().items().filter(e -> {
            var codePath = categoryCodePath(e.absUrl("href"));
            if (!codePath.contains(selectedCodePath)) return false;
            return codePath.size() - selectedCodePath.size() == 1;
        }).map(e -> {
            var url = e.absUrl("href");
            return Link
                    .builder()
                    .code(categoryCode(e.absUrl("href")))
                    .name(e.text())
                    .url(url)
                    .source(selected)
                    .build();
        }).toList();
    }

    private class LeftMenu {
        final Map<String, Element> blocks;

        LeftMenu() {
            this.blocks = leftMenuBlocks().collect(toMap(e -> e.attr("data-index"), identity()));
        }

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
}