package pactas.models.webhooks;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pactas.PactasJsonUtils.readObjectFromResource;

public class WebhookContractCreatedTest {

    private static final Webhook WEBHOOK = readObjectFromResource("pactas-webhook-contract.json", Webhook.class);

    @Test
    public void parsesWebhookInformation() throws Exception {
        final WebhookContractCreated contractCreated = (WebhookContractCreated) WEBHOOK;
        assertThat(contractCreated.getContractId()).isEqualTo("58e3a4af14aa010f3864eda1");
    }
}
