package GroceryFamily.GroceryElders.api.client;

import GroceryFamily.GroceryElders.domain.Page;
import GroceryFamily.GroceryElders.domain.Product;
import io.micrometer.common.lang.NonNullApi;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@NonNullApi
public class ProductAPIClient {
    private static final int DEFAULT_PAGE_SIZE = 100;

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

    @Jacksonized
    @SuperBuilder(toBuilder = true)
    private static final class ProductPage extends Page<Product> {}

    private final String uri;
    private final RestTemplate api = new RestTemplate();

    public ProductAPIClient(String uri) {
        this.uri = uri;
        api.setErrorHandler(ERROR_HANDLER);
    }

    public Stream<Product> listAll() {
        var iterator = new PageIterator<>(this::list, this::list);
        return stream(spliteratorUnknownSize(iterator, ORDERED), false)
                .flatMap(page -> page.content.stream());
    }

    public Page<Product> list() {
        return list(DEFAULT_PAGE_SIZE);
    }

    public Page<Product> list(int pageSize) {
        var url = format("%s/products?pageSize=%s", uri, pageSize);
        var request = new HttpEntity<>(headers());
        return body(api.exchange(url, GET, request, ProductPage.class));
    }

    public Page<Product> list(String pageToken) {
        var url = format("%s/products?pageToken=%s", uri, pageToken);
        var request = new HttpEntity<>(headers());
        return body(api.exchange(url, GET, request, ProductPage.class));
    }

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