package GroceryFamily.GroceryDad.Parser;

import GroceryFamily.GroceryDad.similarity.ProductFilter;
import GroceryFamily.GroceryElders.model.Measurement;
import GroceryFamily.GroceryElders.model.Product;
import GroceryFamily.GroceryElders.service.GroceryInfoService;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
abstract class WebParser {

    protected String url;
    protected WebDriver driver;
    protected List<Product> onThePage;
    protected List<String> namesFromDB;
    protected List<String> quantityFromDB;

    @Autowired
    protected GroceryInfoService groceryInfoService;

    WebParser(WebDriver driver, GroceryInfoService groceryInfoService) {
        this.driver = driver;
        this.groceryInfoService = groceryInfoService;
    }

    //Setters/Getters
    public List<String> getNamesFromDB() {
        return namesFromDB = groceryInfoService.getProductName();
    }

    public List<String> getQuantityFromDB() {
        return quantityFromDB = groceryInfoService.getQuantity();
    }

    public List<Product> getOnThePage() {
        return onThePage;
    }

    public void setOnThePage(List<Product> onThePage) {
        this.onThePage = onThePage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    //Methods
    void scrapeWebSite() {
    }

    void logIntoWebSite(String url) {
        driver.get(url);
    }

    //for work for Rimi and Barbora it is good.
    // For Prisma I don't need consent to save cookie
    public void removeCookiePopup() {
        WebElement element =
                driver.findElement(By
                        .id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));

        element.click();
    }

    // Filter for price
    public Product getCheapestProduct(List<Product> products) {
        return ProductFilter.cheaperPrice(products);
    }

    //Filter product products, by product name from the database
    public List<Product> nameFilter(List<Product> products, String nameFromDB) {
        ProductFilter.containsAllWords(products, nameFromDB);

        for (Product product : products)
            if (product != null) {
                System.out.println("nameFilter" + product);
            } else {
                System.out.println("nameFilter don`t work");
            }

        return products;
    }

    //get List of product on the page. Parsing for getting all information
    // about product on the searching page
    abstract public List<String> getGroceriesJsonInfoOnThePage(String cssSelector);

    //Searching all price per unit on the page
    abstract public Measurement getUnitPrice(int numberOfElement, List<String[]> measurement);

    //Sort for getting Product obj
    abstract public List<Product> getProductsFromPage(List<String> info);

    // Enter to the search bar text.
    public void searching(String name) {
        WebElement element = driver.findElement(By.id("fti-search"));
        element.sendKeys(name);
        element.sendKeys(Keys.ENTER);
    }

    // to enter the Barbora, you need to log in.
    public void addToCard(Product product) {

        WebElement productDiv = driver.findElement(By.linkText(product.getName()));
        WebElement button = driver.findElement(By.xpath("//button[normalize-space()='" + "Ostukorvi" + "']"));
        button.click();
    }
}
