package GroceryFamily.GroceryDad.scraper.page;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@ToString
@EqualsAndHashCode
public class Path<SEGMENT> {
    private final LinkedList<SEGMENT> segments;

    private Path() {
        this(new LinkedList<>());
    }

    private Path(List<SEGMENT> segments) {
        this(new LinkedList<>(segments));
    }

    private Path(LinkedList<SEGMENT> segments) {
        this.segments = segments;
    }

    public Path<SEGMENT> precededBy(SEGMENT head) {
        var values = new LinkedList<>(this.segments);
        values.addFirst(head);
        return new Path<>(values);
    }

    public Path<SEGMENT> followedBy(SEGMENT tail) {
        var values = new LinkedList<>(this.segments);
        values.addLast(tail);
        return new Path<>(values);
    }

    public Path<SEGMENT> filter(Predicate<SEGMENT> predicate) {
        var filtered = new LinkedList<SEGMENT>();
        segments().stream().filter(predicate).forEach(filtered::add);
        return new Path<>(filtered);

    }

    public SEGMENT head() {
        if (segments.isEmpty()) throw new NoSuchElementException();
        return segments.getFirst();
    }

    public SEGMENT tail() {
        if (segments.isEmpty()) throw new NoSuchElementException();
        return segments.getLast();
    }

    public List<SEGMENT> segments() {
        return List.copyOf(segments);
    }

    public boolean contains(Path<SEGMENT> path) {
        if (size() < path.size()) return false;
        return segments.subList(0, path.size()).equals(path.segments);
    }

    public int size() {
        return segments.size();
    }

    public boolean isEmpty() {
        return segments.isEmpty();
    }

    public Path<SEGMENT> parent() {
        return new Path<>(segments.subList(0, segments.size() - 1));
    }

    public static <SEGMENT> Path<SEGMENT> of(SEGMENT[] segments) {
        return new Path<>(List.of(segments));
    }

    public static <SEGMENT> Path<SEGMENT> of(List<SEGMENT> segments) {
        return new Path<>(segments);
    }

    public static <SEGMENT> Path<SEGMENT> empty() {
        return new Path<>();
    }
}