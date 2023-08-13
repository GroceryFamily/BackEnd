package GroceryFamily.GroceryDad.scraper.page.context;

import GroceryFamily.GroceryDad.scraper.cache.Cache;
import GroceryFamily.GroceryDad.scraper.page.Context;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.view.Path;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.stream.Stream;

import static GroceryFamily.GroceryDad.scraper.page.Page.sleep;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.apache.commons.lang3.StringUtils.substringAfter;

public class BarboraContext extends Context {
    private boolean initialized;

    public BarboraContext(Cache.Factory cacheFactory) {
        super(cacheFactory);
    }

    @Override
    protected void waitUntilReady() {
        if (initialized) return;
        acceptOrRejectCookies();
        switchToEnglish();
        initialized = true;
    }

    @Override
    protected Stream<Link> categoryLinks(Document document, Link selected) {
        return document
                .select("a[class*=category]")
                .stream()
                .filter(Element::hasText)
                .map(BarboraContext::categoryLink);
    }

    private static Link categoryLink(Element e) {
        return Link
                .builder()
                .codePath(categoryLinkCodePath(e))
                .name(e.text())
                .url(e.absUrl("href"))
                .build();
    }

    private static Path<String> categoryLinkCodePath(Element e) {
        return Path.of(substringAfter(e.attr("href"), "/").split("/"));
    }

    private static void acceptOrRejectCookies() {
        $("#CybotCookiebotDialogBodyLevelButtonLevelOptinDeclineAll").shouldBe(visible).click();
    }

    private static void switchToEnglish() {
        topMenuItemElement("Kaubavalik").shouldBe(visible);
        languageSelectElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).hover();
        sleep();
        englishLanguageElement().shouldBe(visible).click();
        topMenuItemElement("Products").shouldBe(visible);
    }

    private static SelenideElement topMenuItemElement(String name) {
        return topMenuElement().$$("li[id*=fti-desktop-menu-item]").findBy(text(name));
    }

    private static SelenideElement topMenuElement() {
        return $("#desktop-menu-placeholder");
    }

    static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }
}