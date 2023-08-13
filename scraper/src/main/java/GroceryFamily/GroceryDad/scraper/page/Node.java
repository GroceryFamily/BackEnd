package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Node {
    public final Link link;
    private final Context context;
    private Node parent;
    private final Map<String, Node> children = new HashMap<>();

    @Builder
    Node(Link link, Context context) {
        this.link = link;
        this.context = context;
    }

    public Path<String> codePath() {
        return link.codePath;
    }

    public Path<String> namePath() {
        var path = Path.<String>empty();
        var node = this;
        while (node.parent != null) {
            path = path.precededBy(node.link.name);
            node = node.parent;
        }
        return path;
    }

    public void traverse(Consumer<Product> handler) {
        context.select(link);
        var nodes = new HashMap<Path<String>, Node>();
        nodes.put(codePath(), this);
        context.childCategoryLinksSortedByCodePathSize().forEach(link -> {
            var node = Node.builder().link(link).context(context).build();
            var parent = nodes.get(link.codePath.parent());
            parent.children.put(link.code(), node);
            node.parent = parent;
            nodes.put(link.codePath, node);
        });
        if (isLeaf()) {
            // todo: scrap products
            System.out.printf("Scraping %s%n", namePath());
        } else {
            leaves().forEach(leaf -> leaf.traverse(handler));
        }
    }

    boolean isLeaf() {
        return children.isEmpty();
    }

    List<Node> leaves() {
        if (children.isEmpty()) return List.of(this);
        return children.values().stream().flatMap(child -> child.leaves().stream()).toList();
    }

    public static Node root(String url, Context context) {
        var link = Link.builder().codePath(Path.empty()).name("root").url(url).build();
        return Node.builder().link(link).context(context).build();
    }
}