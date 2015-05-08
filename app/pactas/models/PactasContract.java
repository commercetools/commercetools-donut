package pactas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Currency;

public class PactasContract {
    private final String id;
    private final String customerId;
    private final String planId;
    private final String planVariantId;
    private final String currency;

    private PactasContract(@JsonProperty("Id") String id,
                          @JsonProperty("CustomerId") String customerId,
                          @JsonProperty("PlanId") String planId,
                          @JsonProperty("PlanVariantId") String planVariantId,
                          @JsonProperty("Currency") String currency) {
        this.id = id;
        this.customerId = customerId;
        this.planId = planId;
        this.planVariantId = planVariantId;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPlanId() {
        return planId;
    }

    public String getPlanVariantId() {
        return planVariantId;
    }

    public String getCurrency() {
        return currency;
    }

    public Currency getMonetaryCurrency() {
        return Currency.getInstance(currency);
    }

    @Override
    public String toString() {
        return "PactasContract{" +
                "id='" + id + '\'' +
                ", planId='" + planId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", currency='" + currency + '\'' +
                ", planVariantId='" + planVariantId + '\'' +
                '}';
    }
}
