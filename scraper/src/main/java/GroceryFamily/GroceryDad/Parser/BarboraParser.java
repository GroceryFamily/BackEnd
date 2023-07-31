package GroceryFamily.GroceryDad.Parser;

import GroceryFamily.GroceryElders.model.Measurement;
import GroceryFamily.GroceryElders.model.Product;
import GroceryFamily.GroceryElders.service.GroceryInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BarboraParser extends WebParser {
    private final String barboraUrl = "http://www.barbora.ee";

    public BarboraParser(WebDriver driver, GroceryInfoService groceryInfoService) {
        super(driver, groceryInfoService);
    }

    //main method for that class
    @Override
    public void scrapeWebSite() {
        logIntoWebSite(barboraUrl);
        removeCookiePopup();
        List<String> namesFromDB1 = getNamesFromDB();

        for (String grocery : namesFromDB1) {
            System.out.println(grocery);
            searching(grocery);
            List<Product> products = getProductsFromPage(getGroceriesJsonInfoOnThePage("data-b-for-cart"));

            //// TODO: 21-Jul-23 add price to the DB
            Product product = getCheapestProduct(products);
            System.out.println(product);

        }
    }

    // Create product from Json info and getPricePerUnit
    @Override
    public List<Product> getProductsFromPage(List<String> info) {
        List<Product> products = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<String[]> listPricesPerUnit = getPricesPerUnit();
        int index = 0;
        for (String jsonString : info) {

            try {
                Product product = mapper.readValue(jsonString, Product.class);
                /* todo: fix
                product.setMeasurement(product.getName());
                product.setPricePerUnit(getUnitPrice(index, listPricesPerUnit));
                 */
                products.add(product);
                index++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < namesFromDB.size(); i++) {
            nameFilter(products, namesFromDB.get(i));
        }

        setOnThePage(products);

        return products;
    }

    @Override
    public Product getCheapestProduct(List<Product> products) {
        return super.getCheapestProduct(products);
    }

    //All prices per unit on the page
    public List<String[]> getPricesPerUnit() {

        List<String[]> elements = driver.findElements(By.className("b-product-price--extra"))
                .stream()
                .map(e -> e.getText()
                        .replace("€", "")
                        .replaceAll("/", " ")
                        .split(" "))
                .toList();

        return elements;
    }

    //Create price per unit (need method getPricesPerUnit- that is list of all prices per unit on the page)
    public Measurement getUnitPrice(int numberOfElement, List<String[]> elements) {

        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement = new Measurement();

        for (String[] element : elements) {

            if (element.length >= 2) {
                measurement.setValue(element[0]);
                measurement.setUnit(element[1]);
                measurements.add(measurement);
            }
        }
        return measurements.get(numberOfElement);
    }

    // Need login
    @Override
    public void addToCard(Product product) {
        super.addToCard(product);
    }

    //Takes Json format to create a product (takes - Name, price and unit of measure)
    @Override
    public List<String> getGroceriesJsonInfoOnThePage(String pageSelector) {

        List<String> elements = driver.findElements(By.cssSelector("[" + pageSelector + "]"))
                .stream()
                .map(e -> e.getAttribute(pageSelector))
                .collect(Collectors.toList());

        return elements;
    }
}
