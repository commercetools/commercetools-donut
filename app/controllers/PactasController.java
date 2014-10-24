package controllers;

import com.google.common.base.Optional;
import controllers.BaseController;
import io.sphere.client.SphereResult;
import io.sphere.client.shop.model.*;
import pactas.Pactas;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.webhooks.Webhook;
import pactas.models.webhooks.WebhookAccountCreated;
import play.Configuration;
import play.libs.F;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import sphere.Sphere;
import utils.Util;

import static utils.Util.clearCart;

public class PactasController extends BaseController {
    private final Pactas pactas;

    public PactasController(Sphere sphere, Configuration configuration, Pactas pactas) {
        super(sphere, configuration);
        this.pactas = pactas;
    }

    public void doStuff() {
        clearCart();
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            final F.Promise<PactasContract> contract = pactas.contract(contractId.get())
                    .flatMap(new F.Function<PactasContract, F.Promise<PactasContract>>() {
                        @Override
                        public F.Promise<PactasContract> apply(PactasContract contract) throws Throwable {
                            addSubscribedProductToCart(contract);
                            final F.Promise<PactasCustomer> customer = pactas.customer(contract.getCustomerId())
                                    .map(new F.Function<PactasCustomer, PactasCustomer>() {
                                        @Override
                                        public PactasCustomer apply(PactasCustomer customer) throws Throwable {
                                            createOrder(customer);
                                        }
                                    });
                        }
                    });
        }

    }

    private F.Promise<SphereResult<Cart>> addSubscribedProductToCart(PactasContract contract, PactasCustomer customer) {
        String productId = Util.getProduct().getId();
        int variantId = Util.getVariant(contract.getPhases().get(0).getPlanVariantId()).getId();
        CartUpdate cartUpdate = new CartUpdate()
                .addLineItem(1, productId, variantId)
                .setShippingAddress(customer.getCompleteAddress());
        return sphere().currentCart().updateAsync(cartUpdate);
    }

    private F.Promise<Result> createOrder(PactasCustomer customer) {
        CartUpdate cartUpdate = new CartUpdate().setShippingAddress(customer.getCompleteAddress());
        sphere().currentCart().updateAsync(cartUpdate).map(new F.Function<SphereResult<Cart>, Object>() {
            @Override
            public Object apply(SphereResult<Cart> cartSphereResult) throws Throwable {
                if (cartSphereResult.isSuccess()) {
                    sphere().currentCart().createOrderAsync(PaymentState.Paid);
                }
            }
        }
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public Result executeSubscription() {
        doStuff();
        return ok();
    }

    private Optional<String> parseContractId(Http.Request request) {
        Webhook webhook = Json.fromJson(request.body().asJson(), Webhook.class);
        if (webhook instanceof WebhookAccountCreated) {
            return Optional.of(((WebhookAccountCreated) webhook).getContractId());
        } else {
            return Optional.absent();
        }
    }
}
