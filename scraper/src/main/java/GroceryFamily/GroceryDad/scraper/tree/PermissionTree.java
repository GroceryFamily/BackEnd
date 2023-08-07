package GroceryFamily.GroceryDad.scraper.tree;

import java.util.List;

import static java.util.function.Function.identity;

class PermissionTree extends Tree<String, String> {
    private static final String ANY = "*";

    PermissionTree() {
        super(identity());
    }

    public boolean allowed(List<String> path) {
        var node = root;
        var i = 0;
        while (node != null && i < path.size()) {
            var child = node.children.get(ANY);
            if (child == null) child = node.children.get(path.get(i));
            node = child;
            ++i;
        }
        return node != null;
    }
}