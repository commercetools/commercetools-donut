package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class AvailabilityWrapper {

    private final Boolean isOnStock;

    public AvailabilityWrapper(@JsonProperty("typeId") final Boolean isOnStock) {
        this.isOnStock = requireNonNull(isOnStock);
    }
}
