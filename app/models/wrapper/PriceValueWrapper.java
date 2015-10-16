package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class PriceValueWrapper {

    private final Double amount;
    private final String currencyCode;

    public PriceValueWrapper(@JsonProperty("amount") final Double amount,
                             @JsonProperty("currencyCode") final String currencyCode) {
        this.amount = requireNonNull(amount);
        this.currencyCode = requireNonNull(currencyCode);
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
