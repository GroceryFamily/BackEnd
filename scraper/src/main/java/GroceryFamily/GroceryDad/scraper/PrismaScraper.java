package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.tree.CategoryTreePath;
import GroceryFamily.GroceryDad.scraper.view.NewCategoryView;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Product;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

import static GroceryFamily.GroceryDad.scraper.PrismaPage.productElements;
import static GroceryFamily.GroceryDad.scraper.PrismaPage.topCategoryElements;
import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Slf4j
@SuperBuilder
class PrismaScraper extends Scraper {
    @Override
    protected void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
        sleep();
    }

    @Override
    protected void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
        sleep();
    }

    @Override
    protected void scrap(Consumer<Product> handler) {
        NewPrismaPage.runtime(Path.empty()).rootCategoryView().leaves().forEach(leaf -> scrap(leaf, handler));
    }

    private void scrap(NewCategoryView view, Consumer<Product> handler) {
        if (view.isVisited()) return;
        // if (!categoryAllowed(view.namePath())) return; // todo: move on
        view.markVisited();
        open(view.url);
        waitUntilPageLoads();

        var children = NewPrismaPage.runtime(view.codePath).childCategoryViews(view.codePath);
        if (children.isEmpty()) {
            // todo: scrap products
            System.out.printf("Scraping %s%n", view.namePath());
        } else {
            children.forEach(view::addChild);
            view.leaves().forEach(leaf -> scrap(leaf, handler));
        }
    }

    private static List<Product> products(CategoryTreePath path) {
        return productElements()
                .asFixedIterable()
                .stream()
                .map(PrismaProductView::new)
                .map(view -> view.product(path))
                .toList();
    }
}