package GroceryFamily.GroceryElders.api.client;

import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class APIClientException extends RuntimeException {
    public final int httpStatusCode;

    APIClientException(ClientHttpResponse response) throws IOException {
        this(response.getStatusCode().value(), message(response.getBody()));
    }

    private APIClientException(int httpStatusCode, String message) {
        super(format("API responded with status %s: %s", httpStatusCode, message));
        this.httpStatusCode = httpStatusCode;
    }

    private static String message(InputStream i) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int l; (l = i.read(buffer)) != -1; ) {
            o.write(buffer, 0, l);
        }
        return o.toString(UTF_8);
    }
}