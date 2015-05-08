package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
