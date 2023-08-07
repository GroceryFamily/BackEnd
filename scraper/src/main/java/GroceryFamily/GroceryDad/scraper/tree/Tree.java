package GroceryFamily.GroceryDad.scraper.tree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class Tree<KEY, VALUE> {
    static class Node<KEY, VALUE> {
        final VALUE value;
        final Map<KEY, Node<KEY, VALUE>> children = new LinkedHashMap<>();

        Node(VALUE value) {
            this.value = value;
        }

        public List<VALUE> leaves() {
            if (children.isEmpty()) return List.of(value);
            return children.values().stream().flatMap(child -> child.leaves().stream()).toList();
        }

        void print(String indent, StringBuilder sb) {
            for (var child : children.values()) {
                sb.append(indent).append(child.value);
                child.print(indent + "  ", sb);
            }
        }
    }

    final Node<KEY, VALUE> root = new Node<>(null);
    private final Function<VALUE, KEY> keyGetter;

    Tree(Function<VALUE, KEY> keyGetter) {
        this.keyGetter = keyGetter;
    }

    public void add(List<VALUE> path) {
        var node = root;
        for (var value : path) {
            var key = keyGetter.apply(value);
            var child = node.children.get(key);
            if (child == null) {
                child = new Node<>(value);
                node.children.put(key, child);
            }
            node = child;
        }
    }

    public List<VALUE> leaves() {
        return root.leaves();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        root.print("\n", sb);
        return sb.toString();
    }
}