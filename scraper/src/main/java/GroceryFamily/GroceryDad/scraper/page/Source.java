package GroceryFamily.GroceryDad.scraper.page;

import lombok.Builder;
import lombok.ToString;

import java.util.function.Function;

@Builder
@ToString
public class Source {
    public final String code;
    public final String name;
    public final SourceType type;
    public final Source parent;

    public Path<String> namePath() {
        return path(source -> source.name);
    }

    public Path<String> codePath() {
        return path(source -> source.code);
    }

    private <SEGMENT> Path<SEGMENT> path(Function<Source, SEGMENT> segment) {
        var path = Path.<SEGMENT>empty();
        var source = this;
        while (source.parent != null) {
            path = path.precededBy(segment.apply(source));
            source = source.parent;
        }
        return path;
    }
}