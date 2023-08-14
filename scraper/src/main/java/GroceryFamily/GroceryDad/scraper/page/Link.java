package GroceryFamily.GroceryDad.scraper.page;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Link {
    public final Path<String> codePath; // todo: source code path, code
    public final String name;
    public final String url;

    public String code() {
        return codePath.tail();
    }
}