package GroceryFamily.GroceryMom.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class PageTokenIO<KEYS> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TypeReference<PageToken<KEYS>> typeReference;

    @SneakyThrows
    public String encode(PageToken<KEYS> modelPageToken) {
        var json = MAPPER.writeValueAsString(modelPageToken);
        var bytes = json.getBytes(UTF_8);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    @SneakyThrows
    public PageToken<KEYS> decode(String domainPageToken) {
        var bytes = domainPageToken.getBytes(UTF_8);
        var json = Base64.getUrlDecoder().decode(bytes);
        return MAPPER.readValue(json, typeReference);
    }
}