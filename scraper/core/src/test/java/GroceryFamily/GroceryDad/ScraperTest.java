package GroceryFamily.GroceryDad;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Multiple Spring Boot applications, some of them utilize ChromeDriver")
@SpringBootTest(classes = Scraper.class)
class ScraperTest {
    @Test
    void test() {
        // todo: implement some better test
    }
}