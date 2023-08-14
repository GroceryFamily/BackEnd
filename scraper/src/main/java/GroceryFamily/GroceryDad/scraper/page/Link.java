package GroceryFamily.GroceryDad.scraper.page;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Link {
    @Deprecated
    public final Path<String> codePath; // todo: source code path, code

    public final Source source;
    public final String code;
    public final String name;
    public final String url;

    @Deprecated
    public String code() {
        return codePath.tail();
    }
}