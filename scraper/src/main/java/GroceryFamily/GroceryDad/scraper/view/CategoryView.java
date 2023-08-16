package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Path;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface CategoryView {
    List<Link> childCategoryLinks();

    default Predicate<Element> childCategory(Path<String> parentCodePath, Function<Element, Path<String>> getCodePath) {
        return e -> {
            var codePath = getCodePath.apply(e);
            if (!codePath.contains(parentCodePath)) return false;
            return codePath.size() - parentCodePath.size() == 1;
        };
    }
}