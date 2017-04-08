package pactas.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import pactas.exceptions.PactasJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookContractCreated;
import play.mvc.Controller;
import play.mvc.Result;
import pactas.PactasJsonUtils;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class PactasWebHookController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(PactasWebHookController.class);

    private final PactasWebHookControllerAction pactasWebHookControllerAction;

    @Inject
    public PactasWebHookController(final PactasWebHookControllerAction pactasWebHookControllerAction) {
        this.pactasWebHookControllerAction = pactasWebHookControllerAction;
    }

    public CompletionStage<Result> createOrderFromSubscription() {
        LOGGER.debug("An order request has been received from Pactas...");
        return parseWebHookContractCreatedFromRequest()
                .map(webhookAccountCreated -> pactasWebHookControllerAction.placeOrder(webhookAccountCreated)
                        .thenApply(order -> {
                            LOGGER.debug("Order created: {}", order);
                            return (Result) ok();
                        }).exceptionally(throwable -> {
                            LOGGER.error("Could not process order request from Pactas", throwable);
                            return internalServerError();
                        }))
                .orElseGet(() -> completedFuture(badRequest()));
    }

    private Optional<WebhookContractCreated> parseWebHookContractCreatedFromRequest() {
        final JsonNode webhookAsJson = request().body().asJson();
        if (webhookAsJson != null) {
            LOGGER.debug("Pactas webhook: " + webhookAsJson);
            try {
                final Webhook webhook = PactasJsonUtils.readObject(Webhook.class, webhookAsJson.toString());
                if (webhook instanceof WebhookContractCreated) {
                    return Optional.of(((WebhookContractCreated) webhook));
                } else {
                    return Optional.empty();
                }
            } catch (PactasJsonException e) {
                LOGGER.error("Could not parse Pactas webhook", e);
            }
        }
        return Optional.empty();
    }
}
