package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class PriceWrapper {

    private final PriceValueWrapper value;

    public PriceWrapper(@JsonProperty("value") final PriceValueWrapper value) {
        this.value = requireNonNull(value);
    }

    public PriceValueWrapper getValue() {
        return value;
    }
}
