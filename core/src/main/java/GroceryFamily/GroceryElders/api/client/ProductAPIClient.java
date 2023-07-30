package GroceryFamily.GroceryElders.api.client;

import GroceryFamily.GroceryElders.domain.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

public class ProductAPIClient {
    private final String uri;
    private final RestTemplate api = new RestTemplate();


    public ProductAPIClient(String uri) {
        this.uri = uri;
    }

    public Product patch(Product patch) {
        var url = format("%s/products/%s", uri, patch.id());
        var request = new HttpEntity<>(patch, headers());
        return api.exchange(url, HttpMethod.POST, request, Product.class).getBody();
    }

    private static HttpHeaders headers() {
        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        return headers;
    }
}