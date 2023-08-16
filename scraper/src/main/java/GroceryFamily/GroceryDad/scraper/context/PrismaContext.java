package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraCategoryView;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraProductListView;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraProductView;
import GroceryFamily.GroceryDad.scraper.context.prisma.PrismaCategoryView;
import GroceryFamily.GroceryDad.scraper.context.prisma.PrismaProductListView;
import GroceryFamily.GroceryDad.scraper.context.prisma.PrismaProductView;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.nodes.Document;

import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.scrollDown;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PrismaContext extends Context {
    private boolean initialized;

    public PrismaContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            switchToEnglish();
            initialized = true;
        }
        if (leftCategoryElements().isEmpty() && !visibleProductListElements().isEmpty()) {
            var count = visibleProductListElements().size();
            while (count < productListSize()) {
                scrollDown();
                visibleProductListElements().shouldHave(sizeGreaterThan(count));
                count = visibleProductListElements().size();
            }
        }
    }

    @Override
    public List<Link> childCategoryLinks(Document document, Source selected) {
        return PrismaCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .childCategoryLinks();
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return PrismaProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productPageLinks();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return PrismaProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productLinks();
    }

    @Override
    public Product product(Document document, Source selected) {
        return PrismaProductView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .product();
    }

    private static void acceptOrRejectCookies() {
        $("*[class*=cookie-notice] *[class=close-icon]").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        $("*[data-language=en]").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
    }

    private static ElementsCollection topCategoryElements() {
        return $$("#main-navigation a[href*=selection]");
    }

    private static ElementsCollection leftCategoryElements() {
        return $$("*[role=navigation] a[data-category-id]");
    }

    private static ElementsCollection visibleProductListElements() {
        return $$("li[data-ean]");
    }

    private static int productListSize() {
        return Integer.parseInt(productListSizeElement().text());
    }

    private static SelenideElement productListSizeElement() {
        return $("*[class*=category-items] b");
    }
}