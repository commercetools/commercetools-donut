package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public final class JsonUtils {
    private static final org.codehaus.jackson.map.ObjectMapper MAPPER = new org.codehaus.jackson.map.ObjectMapper();

    private JsonUtils() {
    }

    /**
     * Converts the old JSON representation {@link org.codehaus.jackson.JsonNode} into the new Jackson JSON
     * representation {@link com.fasterxml.jackson.databind.JsonNode}.
     * @param jsonOldFormat JSON in the old Jackson format.
     * @return the converted JSON in the new Jackson format.
     */
    public static com.fasterxml.jackson.databind.JsonNode convertToNewFormat(final org.codehaus.jackson.JsonNode jsonOldFormat) {
        final String jsonAsString = jsonOldFormat.toString();
        return Json.parse(jsonAsString);
    }

    /**
     * Converts the new JSON representation {@link com.fasterxml.jackson.databind.JsonNode} into the old Jackson JSON
     * representation {@link org.codehaus.jackson.JsonNode}.
     * @param jsonNewFormat JSON in the new Jackson format.
     * @return the converted JSON in the old Jackson format.
     */
    public static org.codehaus.jackson.JsonNode convertToOldFormat(final com.fasterxml.jackson.databind.JsonNode jsonNewFormat) {
        try {
            return MAPPER.readValue(jsonNewFormat.toString(), org.codehaus.jackson.JsonNode.class);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Gets the object node in the new JSON representation from a JSON node in the old JSON representation.
     * @param jsonNode JSON node in the old Jackson format.
     * @return the converted object node in the new Jackson format.
     */
    public static ObjectNode objectNode(final org.codehaus.jackson.JsonNode jsonNode) {
        ObjectNode json = Json.newObject();
        if (jsonNode != null && jsonNode.isObject()) {
            json = (ObjectNode) convertToNewFormat(jsonNode);
        }
        return json;
    }
}
