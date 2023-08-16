package GroceryFamily.GroceryDad.scraper.context;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraCategoryView;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraProductListView;
import GroceryFamily.GroceryDad.scraper.context.barbora.BarboraProductView;
import GroceryFamily.GroceryDad.scraper.page.Link;
import GroceryFamily.GroceryDad.scraper.page.Source;
import GroceryFamily.GroceryElders.domain.Product;
import com.codeborne.selenide.SelenideElement;
import org.jsoup.nodes.Document;

import java.util.List;

import static GroceryFamily.GroceryDad.scraper.page.PageUtils.sleep;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BarboraContext extends Context {
    private boolean initialized;

    public BarboraContext(GroceryDadConfig.Scraper config) {
        super(config);
    }

    @Override
    protected void initialize() {
        if (!initialized) {
            acceptOrRejectCookies();
            switchToEnglish();
            initialized = true;
        }
    }

    @Override
    public List<Link> childCategoryLinks(Document document, Source selected) {
        return BarboraCategoryView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .childCategoryLinks();
    }

    @Override
    public List<Link> productPageLinks(Document document, Source selected) {
        return BarboraProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productPageLinks();
    }

    @Override
    public List<Link> productLinks(Document document, Source selected) {
        return BarboraProductListView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .productLinks();
    }

    @Override
    public Product product(Document document, Source selected) {
        return BarboraProductView
                .builder()
                .document(document)
                .selected(selected)
                .build()
                .product();
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

    private static SelenideElement englishLanguageElement() {
        return languageSelectElement().$$("li").findBy(text("English"));
    }

    private static SelenideElement languageSelectElement() {
        return $("#fti-header-language-dropdown");
    }
}