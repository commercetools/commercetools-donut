package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import pactas.controllers.PactasWebHookControllerAction;
import io.sphere.sdk.products.ProductProjection;
import org.junit.Test;
import pactas.models.webhooks.WebhookContractCreated;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import static pactas.PactasJsonUtils.readJsonFromResource;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;


public class PactasWebHookControllerTest extends WithServer {

    private static final JsonNode WEBHOOK = readJsonFromResource("pactas-webhook-contract.json");
    private static final ProductProjection PRODUCT = readObjectFromResource("product.json", ProductProjection.class);

    private final PactasWebHookControllerAction controllerAction = mock(PactasWebHookControllerAction.class);

    @Override
    protected Application provideApplication() {
        when(controllerAction.placeOrder(any())).thenReturn(completedFuture(null));
        return new GuiceApplicationBuilder()
                .overrides(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ProductProjection.class).toInstance(PRODUCT);
                        bind(PactasWebHookControllerAction.class).toInstance(controllerAction);
                    }
                }).build();
    }

    @Test
    public void failsIfWrongBodyProvided() throws Exception {
        try (final WSClient wsClient = WS.newClient(testServer.port())) {
            final WSResponse wsResponse = wsClient
                    .url("/order/execute")
                    .post("wrong")
                    .toCompletableFuture().get();
            assertThat(wsResponse.getStatus()).isEqualTo(BAD_REQUEST);
            verify(controllerAction, never()).placeOrder(any());
        }
    }

    @Test
    public void okIfCorrectAccountReceived() throws Exception {
        try (final WSClient wsClient = WS.newClient(testServer.port())) {
            final WSResponse wsResponse = wsClient
                    .url("/order/execute")
                    .post(WEBHOOK)
                    .toCompletableFuture().get();
            assertThat(wsResponse.getStatus()).isEqualTo(OK);
            verify(controllerAction).placeOrder(new WebhookContractCreated("58e3a4af14aa010f3864eda1"));
        }
    }
}
