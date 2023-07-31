package GroceryFamily.GroceryElders.service.mapper;

import java.time.Instant;

public class ProductMapper {
    public static GroceryFamily.GroceryElders.model.Product
    modelProduct(GroceryFamily.GroceryElders.domain.Product domainProduct,
                 Instant ts) {
        var modelProduct = new GroceryFamily.GroceryElders.model.Product();
        return modelProduct
                .setId(domainProduct.id())
                .setNamespace(domainProduct.namespace)
                .setCode(domainProduct.code)
                .setName(domainProduct.name)
                .setPrices(PriceMapper.modelPrices(domainProduct, ts, modelProduct))
                .setTs(ts);
    }

    public static GroceryFamily.GroceryElders.domain.Product
    domainProduct(GroceryFamily.GroceryElders.model.Product modelProduct) {
        return GroceryFamily.GroceryElders.domain.Product
                .builder()
                .namespace(modelProduct.getNamespace())
                .code(modelProduct.getCode())
                .prices(PriceMapper.domainPrices(modelProduct.getPrices()))
                .build();
    }
}