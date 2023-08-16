package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.substringBefore;

@Builder
@ToString
public class Link {
    public final String code;
    public final String name;
    public final String url;
    public final Source source;

    public Path<String> codePath() {
        return source != null ? source.codePath().followedBy(code) : Path.empty();
    }

    public Path<String> namePath() {
        return source != null ? source.namePath().followedBy(name) : Path.empty();
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

    public static Link productList(int pageNo, String url, Source source) {
        var codeSuffix = pageNo == 1 ? "" : "@" + pageNo;
        var code = substringBefore(source.code, "@") + codeSuffix;
        return Link
                .builder()
                .code(code)
                .name(source.name)
                .url(url)
                .source(source.parent)
                .build();
    }
}