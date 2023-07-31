package GroceryFamily.GroceryElders.service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PriceMapper {
    public static List<GroceryFamily.GroceryElders.model.Price>
    modelPrices(GroceryFamily.GroceryElders.domain.Product domainProduct,
                Instant ts,
                GroceryFamily.GroceryElders.model.Product modelProduct) {
        return modelPrices(domainProduct.identifiablePrices(), ts)
                .stream()
                .map(modelPrice -> modelPrice.setProduct(modelProduct))
                .toList();
    }

    public static List<GroceryFamily.GroceryElders.model.Price>
    modelPrices(Map<String, GroceryFamily.GroceryElders.domain.Price> domainPrices,
                Instant ts) {
        List<GroceryFamily.GroceryElders.model.Price> modelPrices = new ArrayList<>();
        domainPrices.forEach((id, price) -> modelPrices.add(modelPrice(id, price, ts)));
        return modelPrices;
    }

    static GroceryFamily.GroceryElders.model.Price
    modelPrice(String id,
               GroceryFamily.GroceryElders.domain.Price domainPrice,
               Instant ts) {
        return new GroceryFamily.GroceryElders.model.Price()
                .setId(id)
                .setUnit(domainPrice.unit)
                .setCurrency(domainPrice.currency)
                .setAmount(domainPrice.amount)
                .setTs(ts);
    }

    static List<GroceryFamily.GroceryElders.domain.Price>
    domainPrices(List<GroceryFamily.GroceryElders.model.Price> modelPrices) {
        return modelPrices.stream().map(PriceMapper::domainPrice).toList();
    }

    static GroceryFamily.GroceryElders.domain.Price
    domainPrice(GroceryFamily.GroceryElders.model.Price modelPrice) {
        return GroceryFamily.GroceryElders.domain.Price
                .builder()
                .unit(modelPrice.getUnit())
                .currency(modelPrice.getCurrency())
                .amount(modelPrice.getAmount())
                .build();
    }
}