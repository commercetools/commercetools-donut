package controllers;

import io.sphere.sdk.orders.Order;
import io.sphere.sdk.products.ProductProjection;
import pactas.Pactas;
import pactas.models.PactasContract;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookAccountCreated;
import play.Configuration;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import services.CartService;
import services.OrderService;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class PactasWebhookController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(PactasWebhookController.class);

    private final Pactas pactas;
    private final OrderService orderService;
    private final CartService cartService;

    @Inject
    public PactasWebhookController(final Configuration configuration, final CartService cartService,
                                   final OrderService orderService, final Pactas pactas,
                                   final ProductProjection productProjection) {
        super(configuration, productProjection);
        this.cartService = requireNonNull(cartService);
        this.orderService = requireNonNull(orderService);
        this.pactas = requireNonNull(pactas);
    }

    public CompletionStage<Result> createOrderFromSubscription() {
        LOG.debug("An order request has been received from Pactas...");
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            return pactas.fetchContract(contractId.get())
                    .thenCompose(pactasContract -> {
                        LOG.debug("Fetched Pactas contract: {}", pactasContract);
                        return createOrderFromContract(pactasContract)
                                .thenApply(order -> {
                                    LOG.debug("Order created: {}", order);
                                    return ok();
                                });
                    });
        }
        return completedFuture(badRequest());
    }

    private Optional<String> parseContractId(final Http.Request request) {
        LOG.debug("Pactas webhook: " + request.body().asText());
        final Webhook webhook = JsonUtils.readObject(Webhook.class, request.body().asText());
        if (webhook instanceof WebhookAccountCreated) {
            return Optional.of(((WebhookAccountCreated) webhook).getContractId());
        } else {
            return Optional.empty();
        }
    }

    private CompletionStage<Order> createOrderFromContract(final PactasContract pactasContract) {
        return pactas.fetchCustomer(pactasContract.getCustomerId())
                .thenCompose(pactasCustomer -> {
                    LOG.debug("Fetched Pactas customer: {}", pactasCustomer);
                    return cartService.createCartWithPactasInfo(productProjection(), pactasContract, pactasCustomer)
                            .thenCompose(cart -> {
                                LOG.debug("Current Cart[cartId={}]", cart.getId());
                                return orderService.createOrder(cart);
                            });
                });
    }
}
