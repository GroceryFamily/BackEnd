package GroceryFamily.GroceryFamilySandbox;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
public class GroceryFamilySandboxApplicationConfig {
    @Bean(destroyMethod = "close")
    WebDriver webDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        return new ChromeDriver(options);
    }
}