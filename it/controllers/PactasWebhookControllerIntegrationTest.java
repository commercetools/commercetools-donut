package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static play.mvc.Http.Status.OK;
import static utils.JsonUtils.readJsonFromResource;

public class PactasWebhookControllerIntegrationTest extends WithServer {

    private static final JsonNode WEBHOOK = readJsonFromResource("pactas-webhook-account.json");

    @Test
    public void testCreateOrderFromSubscription() throws Exception {
        try (final WSClient wsClient = WS.newClient(testServer.port())) {
            final WSResponse wsResponse = wsClient.url("/order/execute")
                    .post(WEBHOOK.toString())
                    .toCompletableFuture().get();
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
        }
    }
}
