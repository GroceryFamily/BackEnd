package GroceryFamily.GroceryDad.scraper.tree;

import GroceryFamily.GroceryDad.scraper.page.Path;

import java.util.List;

public class PermissionTree extends Tree<String, String> {
    private static final String ANY = "*";

    public boolean allowed(Path<String> namePath) {
        return allowed(namePath.segments());
    }

    public boolean allowed(List<String> path) {
        var node = root;
        var i = 1;
        while (node != null && i < path.size()) {
            var child = node.children.get(ANY);
            if (child == null) child = node.children.get(path.get(i));
            node = child;
            ++i;
        }
        return node != null;
    }

    public void add(List<String> path) {
        add(path, "");
    }
}