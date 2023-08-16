package GroceryFamily.GroceryDad.scraper.context.prisma;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.CategoryView;
import GroceryFamily.GroceryDad.scraper.view.View;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@SuperBuilder
public class PrismaCategoryView extends View implements CategoryView {
    @Override
    public List<Link> childCategoryLinks() {
        if (selected.codePath().isEmpty()) {
            return topMenuItems()
                    .map(item -> Link
                            .builder()
                            .code(topMenuItemCode(item))
                            .name(item.text())
                            .url(url(item))
                            .source(selected)
                            .build())
                    .toList();
        }
        return leftMenuItems()
                .map(item -> Link
                        .builder()
                        .code(leftMenuItemCode(item))
                        .name(item.text())
                        .url(url(item))
                        .source(selected)
                        .build())
                .toList();
    }

    private Stream<Element> topMenuItems() {
        return topMenu().stream().flatMap(e -> e.select("a[href*=/selection]").stream());
    }

    private Optional<Element> topMenu() {
        return Optional.ofNullable(document.select("#main-navigation").first());
    }

    private Stream<Element> leftMenuItems() {
        return leftMenu().stream().flatMap(e -> e.select("a[data-category-id]").stream().filter(Element::hasText));
    }

    private Optional<Element> leftMenu() {
        return Optional.ofNullable(document.select("div[role=navigation]").first());
    }

    private static String topMenuItemCode(Element item) {
        return substringAfterLast(substringBeforeLast(url(item), "/"), "/");
    }

    private static String leftMenuItemCode(Element item) {
        return substringAfterLast(url(item), "/");
    }

    private static String url(Element item) {
        return item.absUrl("href");
    }
}