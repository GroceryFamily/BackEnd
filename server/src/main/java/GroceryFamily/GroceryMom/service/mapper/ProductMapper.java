package GroceryFamily.GroceryMom.service.mapper;


import GroceryFamily.GroceryMom.model.PageToken;
import GroceryFamily.GroceryMom.model.PageTokenIO;
import GroceryFamily.GroceryMom.model.PageTokenKeysExtractor;
import GroceryFamily.GroceryMom.model.Product;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static GroceryFamily.GroceryMom.service.mapper.PriceMapper.modelPrices;

public class ProductMapper {
    public static GroceryFamily.GroceryMom.model.Product
    modelProduct(GroceryFamily.GroceryElders.domain.Product domainProduct, Instant ts) {
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

    public static <KEYS> GroceryFamily.GroceryElders.domain.Page<GroceryFamily.GroceryElders.domain.Product>
    domainProductPage(List<GroceryFamily.GroceryMom.model.Product> modelProducts,
                      int pageSize,
                      PageTokenIO<KEYS> pageTokenIO,
                      PageTokenKeysExtractor<Product, KEYS> pageTokenKeysExtractor) {
        if (modelProducts.isEmpty()) return GroceryFamily.GroceryElders.domain.Page.empty();
        var nextPageToken = Optional
                .ofNullable(modelProducts.size() > pageSize ? modelProducts.get(modelProducts.size() - 1) : null)
                .map(pageTokenKeysExtractor::extract)
                .map(nextPageHead -> new PageToken<>(nextPageHead, pageSize))
                .map(pageTokenIO::encode)
                .orElse(null);
        return GroceryFamily.GroceryElders.domain.Page
                .<GroceryFamily.GroceryElders.domain.Product>builder()
                .content(modelProducts.stream().limit(pageSize).map(ProductMapper::domainProduct).toList())
                .nextPageToken(nextPageToken)
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