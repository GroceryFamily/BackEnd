package GroceryFamily.GroceryMom.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class PageToken {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public final String pageHeadId;
    public final int pageSize;

    @SneakyThrows
    public String encode() {
        var json = MAPPER.writeValueAsString(this);
        var bytes = json.getBytes(UTF_8);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    @SneakyThrows
    public static PageToken decode(String token) {
        var bytes = token.getBytes(UTF_8);
        var json = Base64.getUrlDecoder().decode(bytes);
        return MAPPER.readValue(json, PageToken.class);
    }
}