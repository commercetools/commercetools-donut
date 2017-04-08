package pactas.controllers;

import com.google.inject.ImplementedBy;
import io.sphere.sdk.orders.Order;
import pactas.models.webhooks.WebhookAccountCreated;

import java.util.concurrent.CompletionStage;

@ImplementedBy(PactasWebHookControllerActionImpl.class)
public interface PactasWebHookControllerAction {

    CompletionStage<Order> placeOrder(final WebhookAccountCreated webhookAccountCreated);
}