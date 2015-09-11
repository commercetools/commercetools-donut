package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import exceptions.PlanVariantNotFound;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.CustomObjectDraft;
import io.sphere.sdk.customobjects.commands.CustomObjectDeleteCommand;
import io.sphere.sdk.customobjects.commands.CustomObjectUpsertCommand;
import io.sphere.sdk.customobjects.queries.CustomObjectByKeyGet;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CartServiceImpl extends AbstractShopService implements CartService {

    private static final Logger.ALogger LOG = Logger.of(CartServiceImpl.class);

    @Inject
    public CartServiceImpl(final SphereClient sphereClient, final ApplicationLifecycle applicationLifecycle) {
        super(sphereClient, applicationLifecycle);
    }

    @Override
    public Cart getOrCreateCart(final Http.Session session) {
        requireNonNull(session, "'session' must not be null, unable to create or get Cart");
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(String::valueOf)
                .map(cardId -> {
                            final Cart cart = sphereClient().execute(CartByIdGet.of(cardId)).toCompletableFuture().join();
                            LOG.debug("Fetched existing Cart[cartId={}]", cart.getId());
                            return cart;
                        }
                )
                .orElseGet(() -> {
                    final Cart cart = sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
                    LOG.debug("Created new Cart[cartId={}]", cart.getId());
                    session.put(SessionKeys.CART_ID, cart.getId());
                    return cart;
                });
    }

    @Override
    public Cart clearCart(final Cart cart) {
        requireNonNull(cart, "'cart' must not be null, unable to clear Cart");
        LOG.debug("Clearing cart");
        final List<RemoveLineItem> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item, 1);
            return removeLineItem;
        }).collect(Collectors.toList());
        final Cart result = sphereClient().execute(CartUpdateCommand.of(cart, items)).toCompletableFuture().join();
        clearFrequency(result.getId());
        LOG.debug("Cleared Cart[cartId={}]", result.getId());
        return result;
    }

    private void clearFrequency(final String cartId) {
        requireNonNull(cartId, "'cartId' must not be null, unable to clear frequency");
        LOG.debug("Clearing frequency");
        final Optional<CustomObject<JsonNode>> result = Optional.ofNullable(
                sphereClient().execute(CustomObjectByKeyGet.of(PactasKeys.FREQUENCY, cartId)).toCompletableFuture().join());
        if (result.isPresent()) {
            LOG.debug("Fetched existing CustomObject[container={}]", result.get().getContainer());
            final CustomObject<JsonNode> cleared = sphereClient().execute(CustomObjectDeleteCommand.of(PactasKeys.FREQUENCY, cartId)).toCompletableFuture().join();
            LOG.debug("Cleared CustomObject[container={}]", cleared.getContainer());
        }
    }

    @Override
    public void setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant, final int frequency) {
        requireNonNull(cart, "'cart' must not be null");
        requireNonNull(product, "'product' must not be null");

        Optional.of(clearCart(cart)).map((clearedCart) -> {
            final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), frequency);
            final Cart updatedCart = sphereClient().execute(CartUpdateCommand.of(clearedCart, action)).toCompletableFuture().join();
            final CustomObjectDraft<Integer> draft = CustomObjectDraft.ofUnversionedUpsert(PactasKeys.FREQUENCY, updatedCart.getId(), frequency,
                    new TypeReference<CustomObject<Integer>>() {
                    });

            final CustomObject<Integer> customObject = sphereClient().execute(CustomObjectUpsertCommand.of(draft)).toCompletableFuture().join();
            LOG.debug("Setting new or update CustomObject[container={}]", customObject.getContainer());
            return null;
        });
    }

    @Override
    public int getFrequency(final String cartId) {
        requireNonNull(cartId, "'cartId' must not be null");
        final Optional<CustomObject<JsonNode>> result = Optional.ofNullable(
                sphereClient().execute(CustomObjectByKeyGet.of(PactasKeys.FREQUENCY, cartId)).toCompletableFuture().join());
        if (result.isPresent()) {
            return result.get().getValue().asInt();
        }
        return 0;
    }

    @Override
    public Optional<ProductVariant> getSelectedVariant(final Cart cart) {
        requireNonNull(cart, "'cart' must not be null");
        final Optional<ProductVariant> selectedVariant =
                (!cart.getLineItems().isEmpty())
                        ? Optional.ofNullable(cart.getLineItems().get(0).getVariant())
                        : Optional.empty();
        return selectedVariant;
    }

    @Override
    public Cart createCartWithPactasInfo(final ProductProjection product, final PactasContract contract, final PactasCustomer customer) {
        return Optional.of(sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join())
                .map((cart) -> {
                    LOG.debug("Created new Cart[cartId={}] with Pactas info", cart.getId());
                    final ProductVariant variant = getVariantInContract(product, contract);
                    final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), 1);
                    final Cart updatedCart = sphereClient().execute(CartUpdateCommand.of(cart, action)).toCompletableFuture().join();
                    final Address address = AddressBuilder.of(customer.getCompleteAddress()).build();
                    final Cart cartWithAddress = sphereClient().execute(CartUpdateCommand.of(updatedCart, SetShippingAddress.of(address))).toCompletableFuture().join();
                    return cartWithAddress;
                }).orElseThrow(() -> new RuntimeException("Unable to create Order"));
    }

    private ProductVariant getVariantInContract(final ProductProjection product, final PactasContract contract) {
        final String planVariantId = contract.getPlanVariantId();
        final Optional<ProductVariant> variant = variant(product, planVariantId);
        if (variant.isPresent()) {
            return variant.get();
        }
        throw new PlanVariantNotFound(planVariantId);
    }

    private Optional<ProductVariant> variant(final ProductProjection product, final String pactasId) {
        return product.getAllVariants().stream().map(variant -> {
                    final Optional<String> monthly = Optional.of(variant.getAttribute(PactasKeys.ID_MONTHLY))
                            .map((attribute) -> attribute.getValue(AttributeAccess.ofString()));
                    final Optional<String> twoWeeks = Optional.of(variant.getAttribute(PactasKeys.ID_TWO_WEEKS))
                            .map((attribute) -> attribute.getValue(AttributeAccess.ofString()));
                    final Optional<String> weekly = Optional.of(variant.getAttribute(PactasKeys.ID_WEEKLY))
                            .map((attribute) -> attribute.getValue(AttributeAccess.ofString()));

                    if (pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) {
                        return variant;
                    }
                    return null;
                }
        ).findFirst();
    }
}
