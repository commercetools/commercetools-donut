package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;

import static java.util.Objects.requireNonNull;

public class AttributeTypeWrapper extends Base {

    private final String name;

    public AttributeTypeWrapper(@JsonProperty("name") final String name) {
        this.name = requireNonNull(name);
    }

    public String name() {
        return name;
    }
}
