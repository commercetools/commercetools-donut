package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookAccountCreated implements Webhook {
    private final String contractId;

    private WebhookAccountCreated(@JsonProperty("ContractId") String contractId) {
        this.contractId = contractId;
    }

    public String getContractId() {
        return contractId;
    }

    @Override
    public String toString() {
        return "WebhookAccountCreated{" +
                "contractId='" + contractId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebhookAccountCreated that = (WebhookAccountCreated) o;

        if (contractId != null ? !contractId.equals(that.contractId) : that.contractId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return contractId != null ? contractId.hashCode() : 0;
    }
}
