package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;

public class WebhookContractCreated extends Base implements Webhook {

    private final String contractId;

    public WebhookContractCreated(@JsonProperty("ContractId") String contractId) {
        this.contractId = contractId;
    }

    public String getContractId() {
        return contractId;
    }

    @Override
    public String toString() {
        return "WebhookContractCreated{" +
                "contractId='" + contractId + '\'' +
                '}';
    }
}
