package pactas.controllers;

import com.google.inject.Singleton;
import donut.exceptions.PlanVariantNotFoundException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.LineItemDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderFromCartDraft;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;
import pactas.Pactas;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.webhooks.WebhookAccountCreated;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@Singleton
class PactasWebHookControllerActionImpl implements PactasWebHookControllerAction {

    private final static String ID_MONTHLY = "pactas4";
    private final static String ID_TWO_WEEKS = "pactas2";
    private final static String ID_WEEKLY = "pactas1";

    private final SphereClient sphereClient;
    private final Pactas pactas;
    private final ProductProjection product;

    @Inject
    PactasWebHookControllerActionImpl(final SphereClient sphereClient, final Pactas pactas, final ProductProjection product) {
        this.sphereClient = sphereClient;
        this.pactas = pactas;
        this.product = product;
    }

    @Override
    public CompletionStage<Order> placeOrder(final WebhookAccountCreated webhookAccountCreated) {
        return pactas.fetchContract(webhookAccountCreated.getContractId())
                .thenCompose(pactasContract -> pactas.fetchCustomer(pactasContract.getCustomerId())
                        .thenCompose(pactasCustomer -> createCart(pactasContract, pactasCustomer)
                                .thenCompose(this::createOrder)));
    }

    private CompletionStage<Cart> createCart(final PactasContract contract, final PactasCustomer customer) {
        final Integer variantId = findMatchingVariantId(contract)
                .orElseThrow(() -> new PlanVariantNotFoundException(contract.getPlanVariantId()));
        final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR)
                .withLineItems(Collections.singletonList(LineItemDraft.of(product, variantId, 1)))
                .withShippingAddress(customer.getCompleteAddress());
        return sphereClient.execute(CartCreateCommand.of(cartDraft));
    }

    private CompletionStage<Order> createOrder(final Cart cart) {
        final OrderFromCartDraft orderDraft = OrderFromCartDraft.of(cart, null, PaymentState.PAID);
        return sphereClient.execute(OrderFromCartCreateCommand.of(orderDraft));
    }

    private Optional<Integer> findMatchingVariantId(final PactasContract contract) {
        return product.getAllVariants().stream()
                .filter(variant -> Stream.of(ID_MONTHLY, ID_TWO_WEEKS, ID_WEEKLY)
                        .anyMatch(attributeName -> matchesContractId(contract, variant, attributeName)))
                .findAny()
                .map(ProductVariant::getId);
    }

    private boolean matchesContractId(final PactasContract contract, final ProductVariant variant, final String attributeName) {
        return variant.findAttribute(attributeName)
                .map(Attribute::getValueAsString)
                .filter(variantPactasId -> variantPactasId.equals(contract.getPlanVariantId()))
                .isPresent();
    }
}