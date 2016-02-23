package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.JsonException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public final class JsonUtils {
    private static final ObjectMapper MAPPER = newObjectMapper();

    private JsonUtils() {
    }

    public static ObjectMapper newObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T readObjectFromResource(final String resourcePath, final Class<T> clazz) {
        try {
            return MAPPER.readValue(readFromResource(resourcePath), clazz);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static <T> T readObject(final Class<T> clazz, final String input) {
        try {
            return MAPPER.readValue(input, clazz);
        } catch (IOException e) {
            throw new JsonException(input, e);
        }
    }

    public static JsonNode readJsonFromResource(final String input) {
        try {
            final InputStreamReader r = readFromResource(input);
            return MAPPER.readTree(r);
        } catch (IOException e) {
            throw new JsonException(input, e);
        }
    }

    private static InputStreamReader readFromResource(final String resourcePath) throws UnsupportedEncodingException {
        final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        return new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8.name());
    }
}
