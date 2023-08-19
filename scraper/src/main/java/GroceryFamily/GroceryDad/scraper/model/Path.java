package GroceryFamily.GroceryDad.scraper.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ToString
@EqualsAndHashCode
public class Path<SEGMENT> implements Iterable<SEGMENT> {
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

    public Path<SEGMENT> tail() {
        if (segments.isEmpty()) throw new NoSuchElementException();
        return new Path<>(segments.subList(1, segments.size()));
    }

    public SEGMENT last() {
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

    public boolean childOf(Path<SEGMENT> path) {
        return contains(path) && size() - path.size() == 1;
    }

    public boolean parentOf(Path<SEGMENT> path) {
        return path.contains(this) && path.size() - size() == 1;
    }

    @Override
    public Iterator<SEGMENT> iterator() {
        return segments.iterator();
    }

    @Override
    public void forEach(Consumer<? super SEGMENT> action) {
        segments.forEach(action);
    }

    @Override
    public Spliterator<SEGMENT> spliterator() {
        return segments.spliterator();
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

    @SafeVarargs
    public static <SEGMENT> Path<SEGMENT> of(SEGMENT... segments) {
        return new Path<>(List.of(segments));
    }

    public static <SEGMENT> Path<SEGMENT> of(List<SEGMENT> segments) {
        return new Path<>(segments);
    }

    public static <SEGMENT> Path<SEGMENT> empty() {
        return new Path<>();
    }
}