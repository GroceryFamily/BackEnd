package GroceryFamily.GroceryMom.service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class PriceMapper {
    static List<GroceryFamily.GroceryMom.model.Price>
    modelPrices(GroceryFamily.GroceryElders.domain.Product domainProduct,
                Instant ts,
                GroceryFamily.GroceryMom.model.Product modelProduct) {
        var modelPrices = new ArrayList<GroceryFamily.GroceryMom.model.Price>();
        domainProduct.identifiablePrices().forEach((id, domainPrice) -> {
            var modelPrice = modelPrice(id, domainPrice, ts, modelProduct, 0);
            modelPrices.add(modelPrice);
        });
        return modelPrices;
    }

    public static GroceryFamily.GroceryMom.model.Price
    modelPrice(String id,
               GroceryFamily.GroceryElders.domain.Price domainPrice,
               Instant ts,
               GroceryFamily.GroceryMom.model.Product modelProduct,
               int version) {
        return new GroceryFamily.GroceryMom.model.Price()
                .setId(id)
                .setUnit(domainPrice.unit)
                .setCurrency(domainPrice.currency)
                .setAmount(domainPrice.amount)
                .setProduct(modelProduct)
                .setTs(ts)
                .setVersion(version);
    }

    static Set<GroceryFamily.GroceryElders.domain.Price>
    domainPrices(List<GroceryFamily.GroceryMom.model.Price> modelPrices) {
        return modelPrices.stream().map(PriceMapper::domainPrice).collect(toSet());
    }

    static GroceryFamily.GroceryElders.domain.Price
    domainPrice(GroceryFamily.GroceryMom.model.Price modelPrice) {
        return GroceryFamily.GroceryElders.domain.Price
                .builder()
                .unit(modelPrice.getUnit())
                .currency(modelPrice.getCurrency())
                .amount(modelPrice.getAmount())
                .build();
    }
}