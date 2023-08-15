package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryElders.domain.Category;
import lombok.Builder;
import lombok.ToString;

import java.util.function.Function;

@Builder
@ToString
public class Source {
    public final SourceType type;
    public final String code;
    public final String name;
    public final String url;
    public final Source parent;

    public Source root() {
        var source = this;
        while (source.parent != null) source = source.parent;
        return source;
    }

    public Path<String> codePath() {
        return path(source -> source.code);
    }

    public Path<String> namePath() {
        return path(source -> source.name);
    }

    private <SEGMENT> Path<SEGMENT> path(Function<Source, SEGMENT> getSegment) {
        var path = Path.<SEGMENT>empty();
        var source = this;
        while (source.parent != null) {
            var segment = getSegment.apply(source);
            path = path.precededBy(segment);
            source = source.parent;
        }
        return path;
    }

    public static Source category(Link link) {
        return Source
                .builder()
                .type(SourceType.CATEGORY)
                .code(link.code)
                .name(link.name)
                .url(link.url)
                .parent(link.source)
                .build();
    }

    public static Source category(Category category, Source parent) {
        return Source
                .builder()
                .type(SourceType.CATEGORY)
                .code(category.code)
                .name(category.name)
                .url(category.url)
                .parent(parent)
                .build();
    }

    public static Source productList(Link link) {
        return Source
                .builder()
                .type(SourceType.PRODUCT_LIST)
                .code(link.code)
                .name(link.name)
                .url(link.url)
                .parent(link.source)
                .build();
    }

    public static Source product(Link link) {
        return Source
                .builder()
                .type(SourceType.PRODUCT)
                .code(link.code)
                .name(link.name)
                .url(link.url)
                .parent(link.source)
                .build();
    }
}