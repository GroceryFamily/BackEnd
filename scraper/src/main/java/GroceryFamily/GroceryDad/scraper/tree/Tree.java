package GroceryFamily.GroceryDad.scraper.tree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;

import static java.lang.String.format;

public class Tree<KEY, VALUE> {
    public static class Node<KEY, VALUE> {
        public final List<KEY> path;
        public final VALUE value;
        final Map<KEY, Node<KEY, VALUE>> children = new LinkedHashMap<>();

        Node(List<KEY> path, VALUE value) {
            this.path = path;
            this.value = value;
        }

        public List<Node<KEY, VALUE>> leaves() {
            if (children.isEmpty()) return List.of(this);
            return children.values().stream().flatMap(child -> child.leaves().stream()).toList();
        }

        public void forEach(BiConsumer<List<KEY>, VALUE> action, Stack<KEY> path) {
            children.forEach((key, child) -> {
                path.push(key);
                if (child.value != null) action.accept(path, child.value);
                child.forEach(action, path);
                path.pop();
            });
        }

        void print(String indent, StringBuilder sb) {
            for (var child : children.values()) {
                sb.append(indent).append(child.value);
                child.print(indent + "  ", sb);
            }
        }

        @Override
        public String toString() {
            return format("Node(" +
                    "value=" + value +
                    ", children=" + children.size() +
                    ", size=" + size() + "))");
        }

        public int size() {
            return children.size() + children.values().stream().mapToInt(Node::size).sum();
        }
    }

    final Node<KEY, VALUE> root = new Node<>(List.of(), null);

    public Node<KEY, VALUE> get(List<KEY> path) {
        var node = root;
        var i = 0;
        while (node != null && i < path.size()) {
            node = node.children.get(path.get(i));
            ++i;
        }
        return node;
    }

    public boolean exists(List<KEY> path) {
        var node = get(path);
        return node != null && node.value != null;
    }

    public void add(List<KEY> path, VALUE value) {
        var parent = root;
        for (int i = 0; i < path.size() - 1; ++i) {
            var key = path.get(i);
            var child = parent.children.get(key);
            if (child == null) {
                child = new Node<>(path.subList(0, i), null);
                parent.children.put(key, child);
            }
            parent = child;
        }
        var key = path.get(path.size() - 1);
        var node = new Node<>(path, value);
        var existing = parent.children.get(key);
        if (existing != null) node.children.putAll(existing.children);
        parent.children.put(key, node); // todo: test adding leaf nodes before their parents
    }

    public List<Node<KEY, VALUE>> leaves() {
        return root.children.isEmpty() ? List.of() : root.leaves();
    }

    public boolean isEmpty() {
        return root.children.isEmpty();
    }

    public int size() {
        return root.size();
    }

    public void forEach(BiConsumer<List<KEY>, VALUE> action) {
        var path = new Stack<KEY>();
        root.forEach(action, path);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        root.print("\n", sb);
        return sb.toString();
    }
}