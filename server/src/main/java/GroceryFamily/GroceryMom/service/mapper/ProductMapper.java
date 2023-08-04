package GroceryFamily.GroceryMom.service.mapper;

import java.time.Instant;

import static GroceryFamily.GroceryMom.service.mapper.PriceMapper.modelPrices;

public class ProductMapper {
    public static GroceryFamily.GroceryMom.model.Product
    modelProduct(GroceryFamily.GroceryElders.domain.Product domainProduct,
                 Instant ts) {
        var modelProduct = new GroceryFamily.GroceryMom.model.Product();
        return modelProduct
                .setId(domainProduct.id())
                .setNamespace(domainProduct.namespace)
                .setCode(domainProduct.code)
                .setName(domainProduct.name)
                .setPrices(modelPrices(domainProduct, ts, modelProduct))
                .setTs(ts)
                .setVersion(0);
    }

    public static GroceryFamily.GroceryElders.domain.Page<GroceryFamily.GroceryElders.domain.Product>
    domainProductPage(org.springframework.data.domain.Page<GroceryFamily.GroceryMom.model.Product> modelProductPage) {
        return GroceryFamily.GroceryElders.domain.Page
                .<GroceryFamily.GroceryElders.domain.Product>builder()
                .content(modelProductPage.stream().map(ProductMapper::domainProduct).toList())
                .nextPageToken(null) // todo: set the next page token
                .build();
    }

    public static GroceryFamily.GroceryElders.domain.Product
    domainProduct(GroceryFamily.GroceryMom.model.Product modelProduct) {
        return GroceryFamily.GroceryElders.domain.Product
                .builder()
                .namespace(modelProduct.getNamespace())
                .code(modelProduct.getCode())
                .name(modelProduct.getName())
                .prices(PriceMapper.domainPrices(modelProduct.getPrices()))
                .build();
    }
}