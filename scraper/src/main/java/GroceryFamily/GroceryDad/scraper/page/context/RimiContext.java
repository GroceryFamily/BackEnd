package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.Path;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RimiContext extends Context {
    private boolean initialized;

    @Override
    protected void waitUntilReady() {
        if (initialized) return;
        acceptOrRejectCookies();
        initialized = true;
    }

    @Override
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        var buttons = document.select("nav[data-category-menu-container] button");
        return Stream.concat(
                buttons.stream().map(button -> Link
                        .builder()
                        .codePath(categoryCodePath(link(document, button)))
                        .name(button.text())
                        .url(link(document, button).absUrl("href"))
                        .build()),
                buttons.stream().flatMap(button -> submenuLinks(document, button).stream().map(link -> Link
                        .builder()
                        .codePath(categoryCodePath(link))
                        .name(link.text())
                        .url(link.absUrl("href"))
                        .build()))
        );
    }

    private static Path<String> categoryCodePath(Element e) {
        var fragments = e.attr("href").split("/");
        return Path.of(List.of(fragments).subList(4, fragments.length - 2));
    }

    private Element link(Document document, Element button) {
        return submenu(document, button).select("a[class*=base-category-link]").first();
    }

    private Elements submenuLinks(Document document, Element button) {
        return submenu(document, button).select("a").not("[class*=base-category-link]");
    }

    private Element submenu(Document document, Element button) {
        return document.select("*[data-index=" + button.attr("data-target-descendant") + "]").first();
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll")
                .shouldBe(visible)
                .click();
    }
}