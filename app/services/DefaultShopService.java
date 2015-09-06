package services;

import exceptions.PlanVariantNotFound;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangePaymentState;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Logger;
import play.mvc.Http;
import sphere.Sphere;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.notNull;

public final class DefaultShopService implements ShopService {

    private final SphereClient sphereClient;
    private final Sphere deprecatedClient;

    public final static String FREQUENCY = "cart-frequency";
    public final static String ID_MONTHLY = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY = "pactas1";

    public DefaultShopService(final SphereClient sphereClient, final Sphere deprecatedClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
        this.deprecatedClient = requireNonNull(deprecatedClient, "'deprecatedClient' must not be null");
    }

    public Cart getOrCreateCart(final Http.Session session) {
        notNull(session, "Session is null, unable to create or get Cart");
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(String::valueOf)
                .map(cardId -> {
                            final Cart cart = sphereClient.execute(CartByIdGet.of(cardId)).toCompletableFuture().join();
                            Logger.debug("Fetched existing Cart[cartId={}]", cart.getId());
                            return cart;
                        }
                )
                .orElseGet(() -> {
                    final Cart cart = sphereClient.execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
                    Logger.debug("Created new Cart[cartId={}]", cart.getId());
                    session.put(SessionKeys.CART_ID, cart.getId());
                    return cart;
                });
    }

    @Override
    public void setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant, final int frequency) {
        final Cart clearedCart = clearCart(cart);
        final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), frequency);
        final Cart updatedCart = sphereClient.execute(CartUpdateCommand.of(clearedCart, action)).toCompletableFuture().join();

        deprecatedClient.customObjects().set(FREQUENCY, updatedCart.getId(), frequency).get();
    }

    @Override
    public Order createOrder(final Cart cart) {
        final Order order = sphereClient.execute(OrderFromCartCreateCommand.of(cart)).toCompletableFuture().join();
        final ChangePaymentState action = ChangePaymentState.of(PaymentState.PAID);
        final Order updatedOrder = sphereClient.execute(OrderUpdateCommand.of(order, action)).toCompletableFuture().join();
        return updatedOrder;
    }

    @Override
    public Cart createCartWithPactasInfo(final ProductProjection product, final PactasContract contract, final PactasCustomer customer) {
        final ProductVariant variant = getVariantInContract(product, contract);
        final Cart cart = sphereClient.execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
        Logger.debug("Created new Cart[cartId={}] with Pactas info", cart.getId());

        final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), 1);
        final Cart updatedCart = sphereClient.execute(CartUpdateCommand.of(cart, action)).toCompletableFuture().join();

        final Address address = AddressBuilder.of(customer.getCompleteAddress()).build();
        final Cart cartWithAddress = sphereClient.execute(CartUpdateCommand.of(updatedCart, SetShippingAddress.of(address))).toCompletableFuture().join();
        return cartWithAddress;
    }

    @Override
    public Cart clearCart(final Cart cart) {
        final List<RemoveLineItem> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item, 1);
            return removeLineItem;
        }).collect(Collectors.toList());
        final Cart result = sphereClient.execute(CartUpdateCommand.of(cart, items)).toCompletableFuture().join();
        clearFrequency(result.getId());
        return result;
    }

    private void clearFrequency(final String cartId) {
        try {
            deprecatedClient.customObjects().delete(FREQUENCY, cartId).execute();
        } catch (SphereException e) {
            // Assume already removed
            play.Logger.info(e.getMessage(), e);
        }
    }



    @Override
    public Optional<ProductProjection> getProduct() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final CompletionStage<PagedQueryResult<ProductProjection>> resultCompletionStage =
                sphereClient.execute(request);
        final PagedQueryResult<ProductProjection> queryResult = resultCompletionStage.toCompletableFuture().join();
        return queryResult.head();
    }


    @Override
    public int getFrequency(final String cartId) {
        try {
            final com.google.common.base.Optional<CustomObject> frequencyObj = deprecatedClient.customObjects().get(FREQUENCY, cartId).fetch();
            if (frequencyObj.isPresent()) {
                return frequencyObj.get().getValue().asInt();
            }
        } catch (SphereException se) {
            play.Logger.error(se.getMessage(), se);
        }
        return 0;
    }

    @Override
    public Optional<ProductVariant> getSelectedVariant(final Cart cart) {
        final Optional<ProductVariant> selectedVariant =
                (cart.getLineItems().size() > 0)
                        ? Optional.ofNullable(cart.getLineItems().get(0).getVariant())
                        : Optional.empty();
        return selectedVariant;
    }


    private ProductVariant getVariantInContract(final ProductProjection product, final PactasContract contract) {
        final String planVariantId = contract.getPlanVariantId();
        final Optional<ProductVariant> variant = variant(product, planVariantId);
        if (variant.isPresent()) {
            return variant.get();
        }
        throw new PlanVariantNotFound(planVariantId);
    }

    public Optional<ProductVariant> variantFromId(final ProductProjection product, final int variantId) {
        return product.getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    private Optional<ProductVariant> variant(final ProductProjection product, final String pactasId) {
        for (final ProductVariant variant : product.getAllVariants()) {
            final String monthly = variant.getAttribute(ID_MONTHLY).getValue(AttributeAccess.ofString());
            final String twoWeeks = variant.getAttribute(ID_TWO_WEEKS).getValue(AttributeAccess.ofString());
            final String weekly = variant.getAttribute(ID_WEEKLY).getValue(AttributeAccess.ofString());
            if (pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}
