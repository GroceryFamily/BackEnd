package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.scraper.page.Path;
import org.jsoup.nodes.Element;

import java.util.function.Function;
import java.util.function.Predicate;

public class CodePathFilter {
    private final Function<Element, Path<String>> getCodePath;

    private CodePathFilter(Function<Element, Path<String>> getCodePath) {
        this.getCodePath = getCodePath;
    }

    public Predicate<Element> childOf(Path<String> codePath) {
        return e -> codePath.parentOf(getCodePath.apply(e));
    }
}