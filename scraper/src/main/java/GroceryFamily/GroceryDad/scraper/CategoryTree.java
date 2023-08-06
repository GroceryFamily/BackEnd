package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryElders.domain.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CategoryTree {
    static class Node {
        public final Category category;
        public final Map<String, Node> children = new HashMap<>();

        Node(Category category) {
            this.category = category;
        }

        void print(String indent) {
            for (var child : children.values()) {
                System.out.println(indent + child.category);
                child.print(indent + "  ");
            }
        }
    }

    final Node root = new Node(null);

    void add(Stack<Category> stack) {
        var node = root;
        for (var category : stack) {
            var child = node.children.get(category.code);
            if (child == null) {
                child = new Node(category);
                node.children.put(category.code, child);
            }
            node = child;
        }
    }

    void print() {
        root.print("");
    }
}