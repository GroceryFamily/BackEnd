package GroceryFamily.GroceryDad.scraper.model;

public class Allowlist {
    private static final String ANY = "*";

    private final Tree<String, ?> allowed = new Tree<>();

    public boolean allowed(Path<String> path) {
        var node = allowed.root;
        for (var key : path) {
            var child = node.child(ANY);
            if (child != null) return true;
            child = node.child(key);
            if (child == null) return false;
            node = child;
        }
        return true;
    }

    public void put(Path<String> path) {
        if (path.isEmpty()) throw new IllegalArgumentException("Path cannot be empty");
        var head = path.head();
        var tail = path.tail();
        while (!tail.isEmpty()) {
            if (ANY.equals(head)) throw new IllegalArgumentException("Wildcard (*) can only be used at the end");
            head = tail.head();
            tail = tail.tail();
        }
        allowed.put(path, null);
    }
}