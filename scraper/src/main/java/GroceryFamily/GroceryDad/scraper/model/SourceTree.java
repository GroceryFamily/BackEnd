package GroceryFamily.GroceryDad.scraper.model;

import static java.lang.String.format;

public class SourceTree extends Tree<String, Source> {
    @Override
    public String toString() {
        return print((name, source) -> format("[%s] %s", source.type, source.name));
    }
}