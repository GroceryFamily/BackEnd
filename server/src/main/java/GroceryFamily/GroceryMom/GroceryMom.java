package GroceryFamily.GroceryMom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class GroceryMom {
    public static void main(String[] args) {
        SpringApplication.run(GroceryMom.class, args);
    }
}