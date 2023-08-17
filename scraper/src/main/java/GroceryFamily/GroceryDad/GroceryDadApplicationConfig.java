package GroceryFamily.GroceryDad;

import GroceryFamily.GroceryElders.GroceryEldersApplicationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {GroceryEldersApplicationConfig.class})
public class GroceryDadApplicationConfig {}