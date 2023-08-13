package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.scraper.view.Path;
import lombok.Builder;

@Builder
public class Link {
    public final Path<String> codePath;
    public final String name;
    public final String url;

    public String code() {
        return codePath.tail();
    }
}