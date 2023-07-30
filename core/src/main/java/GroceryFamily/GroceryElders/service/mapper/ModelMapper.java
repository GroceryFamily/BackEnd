package GroceryFamily.GroceryElders.service.mapper;

import GroceryFamily.GroceryElders.model.Price;
import GroceryFamily.GroceryElders.model.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ModelMapper {
    public static Product product(GroceryFamily.GroceryElders.domain.Product product, Instant ts) {
        return new Product()
                .setId(product.id())
                .setCode(product.code)
                .setName(product.name)
                .setTs(ts)
                .setPrices(prices(product, ts));
    }

    private static List<Price> prices(GroceryFamily.GroceryElders.domain.Product product, Instant ts) {
        List<Price> prices = new ArrayList<>();
        product.identifiablePrices().forEach((id, price) -> prices.add(price(id, price, ts)));
        return prices;
    }

    public static Price price(String id, GroceryFamily.GroceryElders.domain.Price price, Instant ts) {
        return new GroceryFamily.GroceryElders.model.Price()
                .setId(id)
                .setUnit(price.unit)
                .setCurrency(price.currency)
                .setAmount(price.amount)
                .setTs(ts);
    }
}