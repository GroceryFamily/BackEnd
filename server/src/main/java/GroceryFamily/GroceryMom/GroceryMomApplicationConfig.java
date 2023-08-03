package GroceryFamily.GroceryMom;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
@EnableJpaRepositories
@EnableRetry
public class GroceryMomApplicationConfig {}