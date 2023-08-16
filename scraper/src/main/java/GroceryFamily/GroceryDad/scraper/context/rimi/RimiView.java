package GroceryFamily.GroceryDad.scraper.context.rimi;

import GroceryFamily.GroceryDad.scraper.page.Path;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

class RimiView {
    static String productCode(String url) {
        return substringAfterLast(substringBeforeLast(url, "/p/"), "/");
    }

    static String categoryCode(String url) {
        return categoryCodePath(url).last();
    }

    static Path<String> categoryCodePath(String url) {
        var fragments = substringAfter(url, "://").split("/");
        return Path.of(List.of(fragments).subList(4, fragments.length - 2));
    }
}