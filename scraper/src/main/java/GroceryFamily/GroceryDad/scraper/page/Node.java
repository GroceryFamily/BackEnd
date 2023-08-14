package GroceryFamily.GroceryDad.scraper.page;

import GroceryFamily.GroceryElders.domain.Product;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class Node {
    public final Link link;
    private final Context context;
    private NodeType type;
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
        // todo: check if visited to avoid self-loops
        if (!context.canOpen(namePath())) return;
        var cache = context.cache(codePath());
        var cacheId = parent != null ? link.code() : "root";
        var html = cache.load(cacheId);
        if (html == null) {
            html = context.open(link);
            cache.save(cacheId, html);
        }
        var document = Jsoup.parse(html, link.url);
        var nodes = new HashMap<Path<String>, Node>();
        nodes.put(codePath(), this);
        context.childCategoryLinksSortedByCodePathSize(document, link).forEach(link -> {
            var node = Node.builder().link(link).context(context).build();
            var parent = nodes.get(link.codePath.parent());
            parent.children.put(link.code(), node);
            node.parent = parent;
            nodes.put(link.codePath, node);
        });
        if (isLeaf()) {
            log.info("Scraping {}...", namePath());
            type = NodeType.PRODUCT_LIST;
            context.loadProducts(codePath(), link).forEach(handler);
        } else {
            type = NodeType.CATEGORY;
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