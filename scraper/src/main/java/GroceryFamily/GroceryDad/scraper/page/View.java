package GroceryFamily.GroceryDad.scraper.page;

import java.util.stream.Stream;

public interface View {
    SourceType type();

    Stream<Link> links();
}