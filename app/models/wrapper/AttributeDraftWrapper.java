package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class AttributeDraftWrapper {

    private final String name;
    private final Object value;

    public AttributeDraftWrapper(@JsonProperty("name") final String name, @JsonProperty("value") final Object value) {
        this.name = requireNonNull(name);
        this.value = requireNonNull(value);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
