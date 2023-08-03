package GroceryFamily.GroceryElders;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationPropertiesScan(basePackages = "GroceryFamily")
public class GroceryEldersApplicationConfig {
    @Profile("!test")
    @PropertySource(value = "file:${user.dir}/secrets/application-secrets.yaml")
    static class Secrets {}
}