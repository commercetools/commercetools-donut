package controllers;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.products.ProductProjection;
import pactas.Pactas;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookAccountCreated;
import play.Application;
import play.Logger;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import services.CartService;
import services.OrderService;
import services.ProductService;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Optional;

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

    public F.Promise<Result> createOrderFromSubscription() {
        LOG.debug("An order request has been received from Pactas...");
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            final F.Promise<PactasContract> pactasContractPromise = pactas.fetchContract(contractId.get());
            final F.Promise<Optional<ProductProjection>> productPromise = productService.getProduct();

            return pactasContractPromise.flatMap(pactasContract -> {
                LOG.debug("Fetched Pactas contract: {}", pactasContract);
                final F.Promise<PactasCustomer> customerPromise = pactas.fetchCustomer(pactasContract.getCustomerId());

                return customerPromise.flatMap(pactasCustomer -> {
                    LOG.debug("Fetched Pactas customer: {}", pactasCustomer);

                    return productPromise.flatMap(productProjection -> {
                        final F.Promise<Cart> cartPromise = cartService.createCartWithPactasInfo(productProjection.orElseThrow(ProductNotFoundException::new),
                                pactasContract, pactasCustomer);

                        return cartPromise.flatMap(cart -> {
                            LOG.debug("Current Cart[cartId={}]", cart.getId());

                            final F.Promise<Order> orderPromise = orderService.createOrder(cart);
                            return orderPromise.map(order -> {
                                LOG.debug("Order created: {}", order);
                                return ok();
                            });
                        });
                    });
                });
            });
        }
        return F.Promise.pure(badRequest());
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
