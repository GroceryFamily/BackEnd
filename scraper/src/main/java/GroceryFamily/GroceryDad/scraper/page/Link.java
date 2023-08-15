package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryElders.domain.Category;
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

    @Deprecated
    public Path<String> sourceCodePath() {
        return source != null ? source.codePath() : Path.empty();
    }

    public Path<String> codePath() {
        return source != null ? source.codePath().followedBy(code) : Path.empty();
    }

    public Path<String> namePath() {
        return source != null ? source.namePath().followedBy(name) : Path.empty();
    }

    @Deprecated
    public String code() {
        return codePath.tail();
    }

    public static Link category(Category category, Source source) {
        return Link
                .builder()
                .code(category.code)
                .name(category.name)
                .url(category.url)
                .source(source)
                .build();
    }
}