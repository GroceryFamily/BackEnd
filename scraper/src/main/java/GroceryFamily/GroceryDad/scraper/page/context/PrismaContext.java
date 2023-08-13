package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.Path;
import com.codeborne.selenide.ElementsCollection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class PrismaContext extends Context {
    private boolean initialized;

    @Override
    protected void waitUntilReady() {
        if (initialized) return;
        acceptOrRejectCookies();
        switchToEnglish();
        initialized = true;
    }

    @Override
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        return Stream.concat(topCategoryLinks(document), leftCategoryLinks(document, selected));
    }

    private static Stream<Link> topCategoryLinks(Document document) {
        return document
                .select("#main-navigation a[href*=selection]")
                .stream()
                .filter(Element::hasText)
                .map(PrismaContext::topCategoryLink);
    }

    private static Link topCategoryLink(Element e) {
        return Link
                .builder()
                .codePath(Path.<String>empty().followedBy(topCategoryLinkCode(e)))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static String topCategoryLinkCode(Element e) {
        return substringAfterLast(substringBeforeLast(e.attr("href"), "/"), "/");
    }

    private static Stream<Link> leftCategoryLinks(Document document, Link selected) {
        return document
                .select("*[role=navigation] a[data-category-id]")
                .stream()
                .filter(Element::hasText)
                .map(e -> leftCategoryLink(e, selected));
    }

    private static Link leftCategoryLink(Element e, Link selected) {
        return Link
                .builder()
                .codePath(selected.codePath.followedBy(leftCategoryLinkCode(e)))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static String leftCategoryLinkCode(Element e) {
        return substringAfterLast(e.attr("href"), "/");
    }

    private static void acceptOrRejectCookies() {
        $("*[class*='js-cookie-notice'] *[class='close-icon']").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        $("*[data-language='en']").shouldBe(visible).click();
        topCategoryElements().shouldHave(itemWithText("Groceries"));
    }

    private static ElementsCollection topCategoryElements() {
        return $$("*[id='main-navigation'] a[href*='selection']");
    }
}