package controllers;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.orders.Order;
import pactas.Pactas;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookAccountCreated;
import play.Application;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import services.CartService;
import services.OrderService;
import services.ProductService;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class PactasWebhookController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(PactasWebhookController.class);

    private final Pactas pactas;
    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;

    @Inject
    public PactasWebhookController(final Application application, final CartService cartService,
                                   final OrderService orderService, final ProductService productService,
                                   final Pactas pactas) {
        super(application);
        this.cartService = requireNonNull(cartService);
        this.orderService = requireNonNull(orderService);
        this.productService = requireNonNull(productService);
        this.pactas = requireNonNull(pactas);
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public Result createOrderFromSubscription() {
        Logger.debug("An order request has been received from Pactas...");
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            final PactasContract contract = pactas.fetchContract(contractId.get()).get(2000, TimeUnit.MILLISECONDS);
            LOG.debug("Fetched Pactas contract: {}", contract);
            final PactasCustomer customer = pactas.fetchCustomer(contract.getCustomerId()).get(2000, TimeUnit.MILLISECONDS);
            LOG.debug("Fetched Pactas customer: {}", customer);
            final Cart cart = cartService.createCartWithPactasInfo(productService.getProduct().get(), contract, customer);
            final Order order = orderService.createOrder(cart);
            LOG.debug("Order created: {}", order);
            return ok();
        }
        return badRequest();
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
}
