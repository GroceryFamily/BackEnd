package GroceryFamily.GroceryElders.api.client;

import GroceryFamily.GroceryElders.domain.Product;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@NonNullApi
public class ProductAPIClient {
    private static final ResponseErrorHandler ERROR_HANDLER = new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            throw new APIClientException(response);
        }
    };

    private final String uri;
    private final RestTemplate api = new RestTemplate();

    public ProductAPIClient(String uri) {
        this.uri = uri;
        api.setErrorHandler(ERROR_HANDLER);
    }

    // todo: list methods

    // todo: return optional?
    public Product get(String id) {
        var url = format("%s/products/%s", uri, id);
        var request = new HttpEntity<>(headers());
        return body(api.exchange(url, GET, request, Product.class));
    }

    public void update(Product product) {
        var url = format("%s/products/%s", uri, product.id());
        var request = new HttpEntity<>(product, headers());
        api.exchange(url, POST, request, Product.class);
    }

    private static HttpHeaders headers() {
        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        return headers;
    }

    private static <BODY> BODY body(ResponseEntity<BODY> response) {
        return Optional.ofNullable(response.getBody()).orElseThrow();
    }
}