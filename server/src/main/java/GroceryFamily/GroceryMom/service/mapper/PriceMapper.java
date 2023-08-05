package GroceryFamily.GroceryMom.service.mapper;

import GroceryFamily.GroceryMom.repository.entity.PriceEntity;
import GroceryFamily.GroceryMom.repository.entity.ProductEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class PriceMapper {
    static List<PriceEntity>
    modelPrices(GroceryFamily.GroceryElders.domain.Product domainProduct,
                Instant ts,
                ProductEntity modelProduct) {
        var modelPrices = new ArrayList<PriceEntity>();
        domainProduct.identifiablePrices().forEach((id, domainPrice) -> {
            var modelPrice = modelPrice(id, domainPrice, ts, modelProduct, 0);
            modelPrices.add(modelPrice);
        });
        return modelPrices;
    }

    public static PriceEntity
    modelPrice(String id,
               GroceryFamily.GroceryElders.domain.Price domainPrice,
               Instant ts,
               ProductEntity modelProduct,
               int version) {
        return new PriceEntity()
                .setId(id)
                .setUnit(domainPrice.unit)
                .setCurrency(domainPrice.currency)
                .setAmount(domainPrice.amount)
                .setProduct(modelProduct)
                .setTs(ts)
                .setVersion(version);
    }

    static Set<GroceryFamily.GroceryElders.domain.Price>
    domainPrices(List<PriceEntity> modelPrices) {
        return modelPrices.stream().map(PriceMapper::domainPrice).collect(toSet());
    }

    static GroceryFamily.GroceryElders.domain.Price
    domainPrice(PriceEntity modelPrice) {
        return GroceryFamily.GroceryElders.domain.Price
                .builder()
                .unit(modelPrice.getUnit())
                .currency(modelPrice.getCurrency())
                .amount(modelPrice.getAmount())
                .build();
    }
}