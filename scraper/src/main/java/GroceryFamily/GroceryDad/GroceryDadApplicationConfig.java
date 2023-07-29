package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
public class GroceryDadApplicationConfig {
    @Bean
    WebDriver webDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }
}