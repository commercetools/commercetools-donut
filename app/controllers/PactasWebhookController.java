//package controllers;
//
//import io.sphere.sdk.carts.Cart;
//import io.sphere.sdk.orders.Order;
//import pactas.Pactas;
//import pactas.PactasException;
//import pactas.models.PactasContract;
//import pactas.models.PactasCustomer;
//import pactas.models.webhooks.Webhook;
//import pactas.models.webhooks.WebhookAccountCreated;
//import play.Configuration;
//import play.Logger;
//import play.mvc.Http;
//import play.mvc.Result;
//import services.CartService;
//import services.OrderService;
//import services.ProductService;
//import utils.JsonUtils;
//
//import javax.inject.Inject;
//import java.util.Optional;
//
//import static java.util.Objects.requireNonNull;
//
//public class PactasWebhookController extends BaseController {
//
//    private final Pactas pactas;
//    private final OrderService orderService;
//    private final CartService cartService;
//    private final ProductService productService;
//
//    @Inject
//    public PactasWebhookController(final Configuration configuration, final CartService cartService,
//                                   final OrderService orderService, final ProductService productService,
//                                   final Pactas pactas) {
//        super(configuration);
//        this.cartService = requireNonNull(cartService, "'cartService' must not be null");
//        this.orderService = requireNonNull(orderService, "'orderService' must not be null");
//        this.productService = requireNonNull(productService, "'productService' must not be null");
//        this.pactas = requireNonNull(pactas, "'pactas' must not be null");
//    }
//
//    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
//    public Result createOrderFromSubscription() {
//        Logger.debug("An order request has been received from Pactas...");
//        final Optional<String> contractId = parseContractId(request());
//        if (contractId.isPresent()) {
//            try {
//                final PactasContract contract = pactas.fetchContract(contractId.get()).get();
//                Logger.debug("Fetched Pactas contract: {}", contract);
//                final PactasCustomer customer = pactas.fetchCustomer(contract.getCustomerId()).get();
//                Logger.debug("Fetched Pactas customer: {}", customer);
//                final Cart cart = cartService.createCartWithPactasInfo(productService.getProduct().get(), contract, customer);
//                final Order order = orderService.createOrder(cart);
//                Logger.debug("Order created: {}", order);
//                return ok();
//            } catch (PactasException e) {
//                Logger.error(e.getMessage(), e);
//                return internalServerError();
//            } catch (Exception e) {
//                Logger.error(e.getMessage(), e);
//            }
//        }
//        return badRequest();
//    }
//
//    private Optional<String> parseContractId(final Http.Request request) {
//        Logger.debug("Pactas webhook: " + request.body().asText());
//        final Webhook webhook = JsonUtils.readObject(Webhook.class, request.body().asText());
//        if (webhook instanceof WebhookAccountCreated) {
//            return Optional.of(((WebhookAccountCreated) webhook).getContractId());
//        } else {
//            return Optional.empty();
//        }
//    }
//}
