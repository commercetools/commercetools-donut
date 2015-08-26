package controllers;

import com.google.common.base.Optional;
import exceptions.PlanVariantNotFound;
import io.sphere.client.SphereClientException;
import io.sphere.client.shop.model.*;
import io.sphere.sdk.client.SphereClient;
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
import sphere.Sphere;
import utils.JsonUtils;

public class PactasWebhookController extends BaseController {
    private final Pactas pactas;

    public PactasWebhookController(final Sphere sphere, final SphereClient sphereClient, final Configuration configuration, final Product product, final Pactas pactas) {
        super(sphere, sphereClient, configuration, product);
        this.pactas = pactas;
    }

    /* Method called by Pactas every time an order must be placed (weekly, monthly...) */
    public Result createOrderFromSubscription() {
        Logger.debug("An order request has been received from Pactas...");
        final Optional<String> contractId = parseContractId(request());
        if (contractId.isPresent()) {
            try {
                final PactasContract contract = pactas.fetchContract(contractId.get()).get();
                final PactasCustomer customer = pactas.fetchCustomer(contract.getCustomerId()).get();
                final Cart cart = createCartWithPactasInfo(contract, customer);
                sphere().client().orders().createOrder(cart.getIdAndVersion(), PaymentState.Paid).execute();
                Logger.debug("Order created!");
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
            return Optional.absent();
        }
    }

    private Cart createCartWithPactasInfo(final PactasContract contract, final PactasCustomer customer) {
        final Variant variant = getVariantInContract(contract);
        final Cart cart = sphere().client().carts().createCart(currency()).execute();
        final CartUpdate cartUpdate = new CartUpdate()
                .addLineItem(1, product().getId(), variant.getId())
                .setShippingAddress(customer.getCompleteAddress());
        return sphere().client().carts().updateCart(cart.getIdAndVersion(), cartUpdate).execute();
    }

    private Variant getVariantInContract(final PactasContract contract) {
        final String planVariantId = contract.getPlanVariantId();
        final Optional<Variant> variant = variant(planVariantId);
        if (variant.isPresent()) {
            return variant.get();
        }
        throw new PlanVariantNotFound(planVariantId);
    }
}
