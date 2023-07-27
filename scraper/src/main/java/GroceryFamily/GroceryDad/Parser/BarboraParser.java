package GroceryFamily.GroceryDad.Parser;

import GroceryFamily.GroceryDad.similarity.ProductFilter;
import GroceryFamily.GroceryElders.model.Measurement;
import GroceryFamily.GroceryElders.model.Product;
import GroceryFamily.GroceryElders.service.GroceryInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//ToDO need coke to log in to the site ***************
// // TODO: 24-Jul-23 clear all abstract methods
public class BarboraParser extends WebParser {

    private final String barboraUrl = "http://www.barbora.ee";


    public BarboraParser(WebDriver driver, GroceryInfoService groceryInfoService) {
        super(driver, groceryInfoService);
    }

    //// TODO: 21-Jul-23 try test 
    @Override
    public void scrapeWebSite() {
        logIntoWebSite(barboraUrl);
        removeCookiePopup();
        List<String> namesFromDB1 = getNamesFromDB();

        for (String grocery : namesFromDB1) {
            System.out.println(grocery);
            searching(grocery);
            List<Product> products = getProductsFromPage(getGroceriesJsonInfoOnThePage("data-b-for-cart"));
            for (Product product : products) {
                System.out.println("getProductsFromPage" + product);
            }

            //// TODO: 21-Jul-23 add price to the DB
            Product product = getCheapestProduct(products);
            System.out.println(product);

        }
    }


    //get products from Json

    @Override
    public List<Product> getProductsFromPage(List<String> info) {
        List<Product> products = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<String[]> listPricesPerUnit= getPricesPerUnit();
        int index =0;
        for (String jsonString : info) {

            try {
                Product product = mapper.readValue(jsonString, Product.class);
                product.setMeasurement(product.getName());
                product.setPricePerUnit(getUnitPrice(index,listPricesPerUnit));
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

        //should be removed

        for (Product product : onThePage) {
            if (product != null) {
                System.out.println("setOnThePage" + product);
            } else {
                System.out.println("setOnThePage don`t work");
            }
        }

        return products;
    }


    @Override
    public Product getCheapestProduct(List<Product> products) {
        return super.getCheapestProduct(products);
    }

    //    Searching all price per unit on the page
//    public Measurement getUnitPrice(int numberOfElement) {
//        List<Measurement> elements = driver.findElements(By.className("b-product-price--extra"))
//                .stream()
//                .map(e -> Measurement.setValueUnit(e.getText()
//                        .replace("€", "")
//                        .replaceAll("/", " ")))
//                .toList();
//
//
//        return elements.get(numberOfElement);
//    }
    //test
    public List<String[]> getPricesPerUnit(){

        List<String[]> elements = driver.findElements(By.className("b-product-price--extra"))
                .stream()
                .map(e -> e.getText()
                        .replace("€", "")
                        .replaceAll("/", " ")
                        .split(" "))
                .toList();

        return elements;
    }
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

    @Override
    public void addToCard(Product product) {
        super.addToCard(product);
    }

    @Override
    public List<String> getGroceriesJsonInfoOnThePage(String pageSelector) {
        //work
        List<String> elements = driver.findElements(By.cssSelector("[" + pageSelector + "]"))
                .stream()
                .map(e -> e.getAttribute(pageSelector))
                .collect(Collectors.toList());

        return elements;
    }
}
