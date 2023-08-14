package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryDad.scraper.Scraper;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.context.BarboraContext;
import GroceryFamily.GroceryDad.scraper.page.context.PrismaContext;
import GroceryFamily.GroceryDad.scraper.page.context.RimiContext;
import GroceryFamily.GroceryDad.scraper.tree.PermissionTree;
import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Namespace;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@SpringBootApplication
class GroceryDad implements CommandLineRunner {
    private final Collection<Scraper> scrapers = new ArrayList<>();

    GroceryDad(GroceryDadConfig dadConfig, WebDriver driver) {
        var client = new ProductAPIClient(dadConfig.api.uri);
        for (var name : dadConfig.enabled) {
            var config = dadConfig.scrapers.get(name);
            if (config == null) throw new IllegalArgumentException(format("Missing '%s' config", name));
            scrapers.add(Scraper
                    .builder()
                    .config(config)
                    .driver(driver)
                    .client(client)
                    .categoryPermissions(buildCategoryPermissionTree(config))
                    .context(context(config))
                    .build());
        }
    }

    @Override
    public void run(String... args) {
        scrapers.forEach(Scraper::scrap);
    }

    public static void main(String... args) {
        SpringApplication.run(GroceryDad.class, args);
    }

    private static Context context(GroceryDadConfig.Scraper config) {
        return switch (config.namespace) {
            case Namespace.BARBORA -> new BarboraContext(config);
            case Namespace.PRISMA -> new PrismaContext(config);
            case Namespace.RIMI -> new RimiContext(config);
            default -> throw new UnsupportedOperationException(format("Unrecognized namespace '%s'", config.namespace));
        };
    }

    private static PermissionTree buildCategoryPermissionTree(GroceryDadConfig.Scraper config) {
        var tree = new PermissionTree();
        config.categories.forEach(tree::add);
        return tree;
    }
}