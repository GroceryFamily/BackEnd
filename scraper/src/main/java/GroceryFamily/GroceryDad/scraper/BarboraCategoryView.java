package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.ToString;
import org.jsoup.nodes.Element;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringAfter;

//@ToString(callSuper = true)
class BarboraCategoryView {//extends NewCategoryView {
//    final List<String> codePath;

//    BarboraCategoryView(Element e) {
//        super(category(e), absoluteURL(e));
//        this.codePath = codePath(e);
//    }

    private static Category category(Element e) {
        return Category
                .builder()
                .code(code(e))
                .name(name(e))
                .build();
    }

    private static String code(Element e) {
        var codePath = codePath(e);
        return codePath.get(codePath.size() - 1);
    }

    private static String name(Element e) {
        return e.text();
    }

    private static List<String> codePath(Element e) {
        return List.of(substringAfter(relativeURL(e), "/").split("/"));
    }

    private static String relativeURL(Element e) {
        return e.attr("href");
    }

    private static String absoluteURL(Element e) {
        return e.absUrl("href");
    }
}