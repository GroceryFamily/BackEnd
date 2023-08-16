package GroceryFamily.GroceryDad.scraper.view;

import GroceryFamily.GroceryDad.scraper.page.Path;
import GroceryFamily.GroceryDad.scraper.page.Source;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.function.Function;
import java.util.function.Predicate;

@SuperBuilder
public class View {
    protected final Document document;
    protected final Source selected;

    protected static CodePathFilter codePathFilter(Function<Element, Path<String>> getCodePath) {
        return new CodePathFilter(getCodePath);
    }

    protected static class CodePathFilter {
        private final Function<Element, Path<String>> getCodePath;

        private CodePathFilter(Function<Element, Path<String>> getCodePath) {
            this.getCodePath = getCodePath;
        }

        public Predicate<Element> childOf(Path<String> codePath) {
            return e -> codePath.parentOf(getCodePath.apply(e));
        }
    }
}