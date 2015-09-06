package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.orders.Order;
import pactas.Pactas;
import pactas.PactasException;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookAccountCreated;
import play.Configuration;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import services.ShopService;
import utils.JsonUtils;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class PactasWebhookController extends BaseController {
    private final Pactas pactas;

    public PactasWebhookController(final Configuration configuration, final ShopService cartService, final Pactas pactas) {
        super(configuration, cartService);
        this.pactas = requireNonNull(pactas, "'pactas' must not be null");
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public Result createOrderFromSubscription() {
        Logger.debug("An order request has been received from Pactas...");
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            try {
                final PactasContract contract = pactas.fetchContract(contractId.get()).get();
                Logger.debug("Fetched Pactas contract: {}", contract);
                final PactasCustomer customer = pactas.fetchCustomer(contract.getCustomerId()).get();
                Logger.debug("Fetched Pactas customer: {}", customer);
                final Cart cart = shopService().createCartWithPactasInfo(product(), contract, customer);
                final Order order = shopService().createOrder(cart);
                Logger.debug("Order created: {}", order);
                return ok();
            } catch (SphereClientException e) {
                Logger.error(e.getMessage(), e);
                return internalServerError();
            } catch (PactasException e) {
                Logger.error(e.getMessage(), e);
            }
        }
        return badRequest();
    }

    private Optional<String> parseContractId(final Http.Request request) {
        Logger.debug("Pactas webhook: " + request.body().asText());
        final Webhook webhook = JsonUtils.readObject(Webhook.class, request.body().asText());
        if (webhook instanceof WebhookAccountCreated) {
            return Optional.of(((WebhookAccountCreated) webhook).getContractId());
        } else {
            return Optional.empty();
        }
    }
}
