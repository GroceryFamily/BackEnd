package GroceryFamily.GroceryElders.service.mapper;

import GroceryFamily.GroceryElders.domain.Price;
import GroceryFamily.GroceryElders.domain.Product;

import java.util.List;

public class DomainMapper {
    public static Product product(GroceryFamily.GroceryElders.model.Product product) {
        return Product
                .builder()
                .namespace(product.getNamespace())
                .code(product.getCode())
                .prices(prices(product.getPrices()))
                .build();
    }

    private static List<Price> prices(List<GroceryFamily.GroceryElders.model.Price> prices) {
        return prices.stream().map(DomainMapper::price).toList();
    }

    private static Price price(GroceryFamily.GroceryElders.model.Price price) {
        return Price
                .builder()
                .unit(price.getUnit())
                .currency(price.getCurrency())
                .amount(price.getAmount())
                .build();
    }
}