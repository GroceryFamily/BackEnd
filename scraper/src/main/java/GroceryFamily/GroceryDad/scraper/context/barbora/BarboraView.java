package GroceryFamily.GroceryDad.scraper.context.barbora;

import GroceryFamily.GroceryDad.scraper.page.Path;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

class BarboraView {
    static String productCode(String url) {
        return substringAfterLast(url, "/");
    }

    static String categoryCode(String url) {
        return categoryCodePath(url).last();
    }

    static Path<String> categoryCodePath(String url) {
        var fragments = substringAfter(url, "://").split("/");
        return Path.of(List.of(fragments).subList(1, fragments.length));
    }
}