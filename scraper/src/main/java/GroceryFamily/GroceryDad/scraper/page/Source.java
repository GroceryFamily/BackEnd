package GroceryFamily.GroceryDad.scraper.page;

import lombok.Builder;
import lombok.ToString;

import java.util.function.Function;

@Builder
@ToString
public class Source {
    public final SourceType type;
    public final String code;
    public final String name;
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
}