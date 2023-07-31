package GroceryFamily.GroceryElders.api.client;

import GroceryFamily.GroceryElders.domain.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

public class ProductAPIClient {
    private final String uri;
    private final RestTemplate api = new RestTemplate();

    public ProductAPIClient(String uri) {
        this.uri = uri;
    }

    // todo: list methods

    public Product get(String id) {
        var url = format("%s/products/%s", uri, id);
        var request = new HttpEntity<>(headers());
        return api.exchange(url, GET, request, Product.class).getBody();
    }

    public Product update(Product product) {
        var url = format("%s/products/%s", uri, product.id());
        var request = new HttpEntity<>(product, headers());
        return api.exchange(url, POST, request, Product.class).getBody();
    }

    private static HttpHeaders headers() {
        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        return headers;
    }
}