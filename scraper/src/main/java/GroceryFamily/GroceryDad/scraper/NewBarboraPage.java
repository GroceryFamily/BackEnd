package GroceryFamily.GroceryDad.scraper;

import GroceryFamily.GroceryDad.scraper.page.NewPage;
import GroceryFamily.GroceryDad.scraper.page.link.CategoryLink;
import GroceryFamily.GroceryDad.scraper.view.Path;
import GroceryFamily.GroceryElders.domain.Category;
import lombok.experimental.SuperBuilder;
import org.jsoup.nodes.Element;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.substringAfter;

@SuperBuilder
class NewBarboraPage extends NewPage {
//    @Override
    protected Stream<CategoryLink> categoryLinks() {
        return document
                .select("a[class*=category]")
                .stream()
                .filter(Element::hasText)
                .map(NewBarboraPage::categoryLink);
    }

    private static CategoryLink categoryLink(Element e) {
        var relativeURL = e.attr("href");
        var codePath = Path.of(substringAfter(relativeURL, "/").split("/"));
        var category = Category
                .builder()
                .code(codePath.tail())
                .name(e.text())
                .build();
        return CategoryLink
                .builder()
                .codePath(codePath)
                .category(category)
//                .relativeURL(relativeURL)
//                .absoluteURL(e.absUrl("href"))
                .build();
    }
//
//    public static NewBarboraPage runtime(Path<String> codePath) {
//        return new NewBarboraPage(html(), WebDriverRunner.getWebDriver().getCurrentUrl(), codePath);
//    }
}