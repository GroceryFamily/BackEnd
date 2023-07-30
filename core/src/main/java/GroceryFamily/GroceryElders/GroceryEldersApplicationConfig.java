package GroceryFamily.GroceryElders;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"GroceryFamily"})
@EnableJpaRepositories(basePackages = {"GroceryFamily"})
@EntityScan(basePackages = {"GroceryFamily"})
@ConfigurationPropertiesScan(basePackages = "GroceryFamily")
public class GroceryEldersApplicationConfig {
    @Profile("!test")
    @PropertySource(value = "file:${user.dir}/secrets/application-secrets.yaml")
    static class Secrets {}
}