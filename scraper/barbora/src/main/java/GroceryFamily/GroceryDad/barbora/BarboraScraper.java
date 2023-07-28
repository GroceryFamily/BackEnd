package GroceryFamily.GroceryDad.barbora;

import GroceryFamily.GroceryDad.barbora.Parser.BarboraParser;
import GroceryFamily.GroceryElders.service.GroceryInfoService;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BarboraScraper {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(BarboraScraper.class, args);
        GroceryInfoService groceryInfoService = context.getBean(GroceryInfoService.class);


        WebDriver driver = context.getBean(WebDriver.class);
//  Barbora scarper
        BarboraParser barboraParser = new BarboraParser(driver, groceryInfoService);
        barboraParser.scrapeWebSite();

    }
}
