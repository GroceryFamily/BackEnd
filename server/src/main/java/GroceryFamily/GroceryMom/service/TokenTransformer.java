package GroceryFamily.GroceryMom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TokenTransformer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    public static <DATA> String encode(DATA data) {
        var json = MAPPER.writeValueAsString(data);
        var bytes = json.getBytes(UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @SneakyThrows
    public static <DATA> DATA decode(String token, Class<DATA> clazz) {
        var bytes = token.getBytes(UTF_8);
        var json = Base64.getUrlDecoder().decode(bytes);
        return MAPPER.readValue(json, clazz);
    }
}