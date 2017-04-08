package pactas.models.webhooks;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pactas.PactasJsonUtils.readObjectFromResource;

public class WebhookAccountCreatedTest {

    private static final Webhook WEBHOOK = readObjectFromResource("pactas-webhook-account.json", Webhook.class);

    @Test
    public void parsesWebhookInformation() throws Exception {
        final WebhookAccountCreated accountCreated = (WebhookAccountCreated) WEBHOOK;
        assertThat(accountCreated.getContractId()).isEqualTo("58e3a4af14aa010f3864eda1");
    }
}
