package services;

import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import sphere.Sphere;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.notNull;

public final class DefaultCartService implements CartService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCartService.class);
    private final SphereClient sphereClient;
    private final Sphere deprecatedClient;

    public final static String FREQUENCY    = "cart-frequency";

    public DefaultCartService(final SphereClient sphereClient, final Sphere deprecatedClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
        this.deprecatedClient = requireNonNull(deprecatedClient, "'deprecatedClient' must not be null");
    }

    public Cart createOrGet(Http.Session session) {
        notNull(session, "Session is null, unable to create or get Cart");
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(String::valueOf)
                .map(cardId -> {
                            final Cart cart = sphereClient.execute(CartByIdGet.of(cardId)).toCompletableFuture().join();
                            LOG.debug("Fetched existing Cart[cartId={}]", cart.getId());
                            return cart;
                        }
                )
                .orElseGet(() -> {
                    final Cart cart = sphereClient.execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
                    LOG.debug("Created new Cart[cartId={}]", cart.getId());
                    session.put(SessionKeys.CART_ID, cart.getId());
                    return cart;
                });
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
    public void setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant, final int frequency) {
            final Cart clearedCart = clearCart(cart);
            final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), frequency);
            final Cart updatedCart = sphereClient.execute(CartUpdateCommand.of(clearedCart, action)).toCompletableFuture().join();
            deprecatedClient.customObjects().set(FREQUENCY, updatedCart.getId(), frequency).get();
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
}
