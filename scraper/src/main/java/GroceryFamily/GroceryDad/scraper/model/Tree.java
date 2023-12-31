package GroceryFamily.GroceryDad.scraper.model;

import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ToString
public class Tree<KEY extends Comparable<KEY>, VALUE> {
    @ToString
    static class Node<KEY extends Comparable<KEY>, VALUE> {
        private VALUE value;
        private final Map<KEY, Node<KEY, VALUE>> children = new TreeMap<>();

        Node<KEY, VALUE> child(KEY key) {
            return children.get(key);
        }

        private VALUE get(Path<KEY> path) {
            if (path.isEmpty()) return value;
            var child = children.get(path.head());
            return child != null ? child.get(path.tail()) : null;
        }

        private void put(Path<KEY> path, VALUE value) {
            if (path.isEmpty()) {
                this.value = value;
                return;
            }
            var child = children.get(path.head());
            if (child == null) {
                child = new Node<>();
                children.put(path.head(), child);
            }
            child.put(path.tail(), value);
        }

        private int size() {
            return (value != null ? 1 : 0) + children.values().stream().mapToInt(Node::size).sum();
        }

        private void forEach(Consumer<VALUE> action) {
            if (value != null) action.accept(value);
            children.values().forEach(child -> child.forEach(action));
        }

        private List<VALUE> leaves() {
            if (children.isEmpty()) return value != null ? List.of(value) : List.of();
            return children.values().stream().flatMap(child -> child.leaves().stream()).toList();
        }

        private void print(StringBuilder sb, String indent, BiFunction<KEY, VALUE, String> print) {
            children.forEach((key, child) -> {
                sb.append(indent).append(print.apply(key, child.value)).append('\n');
                child.print(sb, indent + "  ", print);
            });
        }
    }

    final Node<KEY, VALUE> root = new Node<>();

    VALUE get(Path<KEY> path) {
        return root.get(path);
    }

    public void put(Path<KEY> path, VALUE value) {
        root.put(path, value);
    }

    int size() {
        return root.size();
    }

    boolean isEmpty() {
        return size() == 0;
    }

    void forEach(Consumer<VALUE> action) {
        root.forEach(action);
    }

    List<VALUE> leaves() {
        return root.leaves();
    }

    String print(BiFunction<KEY, VALUE, String> print) {
        var sb = new StringBuilder();
        root.print(sb, "", print);
        return sb.toString();
    }
}