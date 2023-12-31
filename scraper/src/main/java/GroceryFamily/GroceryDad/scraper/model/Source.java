package GroceryFamily.GroceryDad.scraper.model;

import lombok.Builder;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Builder
@ToString
public class Source {
    public final SourceType type;
    public final String code;
    public final String name;
    public final String url;
    public final Source parent;

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

    public Map<String, String> categories() {
        var categories = new HashMap<String, String>();
        var source = this;
        while (source.parent != null) {
            if (source.type == SourceType.CATEGORY) {
                categories.put(source.code, source.name);
            }
            source = source.parent;
        }
        return categories;
    }

    public Link link() {
        return Link
                .builder()
                .code(code)
                .name(name)
                .url(url)
                .source(parent)
                .build();
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