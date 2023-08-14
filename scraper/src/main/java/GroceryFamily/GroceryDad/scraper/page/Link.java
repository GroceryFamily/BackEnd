package GroceryFamily.GroceryDad.scraper.page;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Link {
    @Deprecated
    public final Path<String> codePath; // todo: source code path, code

    public final String code;
    public final String name;
    public final String url;
    public final Source source;

    public Path<String> sourceCodePath() {
        return source != null ? source.codePath() : Path.empty();
    }

    public Path<String> namePath() {
        return sourceNamePath().followedBy(name);
    }

    private Path<String> sourceNamePath() {
        return source != null ? source.namePath() : Path.empty();
    }

    @Deprecated
    public String code() {
        return codePath.tail();
    }
}