package pactas.models.webhooks;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.JsonUtils.readObjectFromResource;

public class WebhookAccountCreatedTest {
    public static final Webhook WEBHOOK = readObjectFromResource("pactas-webhook-account.json", Webhook.class);

    @Test
    public void parsesWebhookInformation() throws Exception {
        final WebhookAccountCreated accountCreated = (WebhookAccountCreated) WEBHOOK;
        assertThat(accountCreated.getContractId()).isEqualTo("51d970c8eb596a1168df119a");
    }
}
