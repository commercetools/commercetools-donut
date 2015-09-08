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
import play.mvc.Http;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CartServiceImpl extends AbstractShopService implements CartService {

    public CartServiceImpl(final SphereClient sphereClient) {
        super(sphereClient);
    }

    @Override
    public Cart getOrCreateCart(final Http.Session session) {
        requireNonNull(session, "'session' must not be null, unable to create or get Cart");
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(String::valueOf)
                .map(cardId -> {
                            final Cart cart = sphereClient().execute(CartByIdGet.of(cardId)).toCompletableFuture().join();
                            Logger.debug("Fetched existing Cart[cartId={}]", cart.getId());
                            return cart;
                        }
                )
                .orElseGet(() -> {
                    final Cart cart = sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
                    Logger.debug("Created new Cart[cartId={}]", cart.getId());
                    session.put(SessionKeys.CART_ID, cart.getId());
                    return cart;
                });
    }

    @Override
    public Cart clearCart(final Cart cart) {
        requireNonNull(cart, "'cart' must not be null, unable to clear Cart");
        Logger.debug("Clearing cart");
        final List<RemoveLineItem> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item, 1);
            return removeLineItem;
        }).collect(Collectors.toList());
        final Cart result = sphereClient().execute(CartUpdateCommand.of(cart, items)).toCompletableFuture().join();
        clearFrequency(result.getId());
        return result;
    }

    private void clearFrequency(final String cartId) {
        requireNonNull(cartId, "'cartId' must not be null, unable to clear frequency");
        Logger.debug("Clearing frequency");
        final Optional<CustomObject<JsonNode>> result = Optional.ofNullable(
                sphereClient().execute(CustomObjectByKeyGet.of(ShopKeys.FREQUENCY, cartId)).toCompletableFuture().join());
        if (result.isPresent()) {
            Logger.debug("Fetched existing CustomObject: {}", result);
            final CustomObject<JsonNode> cleared =  sphereClient().execute(CustomObjectDeleteCommand.of(ShopKeys.FREQUENCY, cartId)).toCompletableFuture().join();
            Logger.debug("Cleared CustomObject: {}", cleared);
        }
    }

    @Override
    public void setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant, final int frequency) {
        requireNonNull(cart, "'cart' must not be null");
        requireNonNull(product, "'product' must not be null");
        final Cart clearedCart = clearCart(cart);
        final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), frequency);
        final Cart updatedCart = sphereClient().execute(CartUpdateCommand.of(clearedCart, action)).toCompletableFuture().join();

        final CustomObjectDraft<Integer> draft = CustomObjectDraft.ofUnversionedUpsert(ShopKeys.FREQUENCY, updatedCart.getId(), frequency,
                new TypeReference<CustomObject<Integer>>() {
                });

        final CustomObject<Integer> customObject = sphereClient().execute(CustomObjectUpsertCommand.of(draft)).toCompletableFuture().join();
        Logger.debug("Setting new or update CustomObject: {}", customObject);
    }

    @Override
    public int getFrequency(final String cartId) {
        requireNonNull(cartId, "'cartId' must not be null");
        final Optional<CustomObject<JsonNode>> result = Optional.ofNullable(
                sphereClient().execute(CustomObjectByKeyGet.of(ShopKeys.FREQUENCY, cartId)).toCompletableFuture().join());
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
        final ProductVariant variant = getVariantInContract(product, contract);
        final Cart cart = sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
        Logger.debug("Created new Cart[cartId={}] with Pactas info", cart.getId());

        final AddLineItem action = AddLineItem.of(product.getId(), variant.getId(), 1);
        final Cart updatedCart = sphereClient().execute(CartUpdateCommand.of(cart, action)).toCompletableFuture().join();

        final Address address = AddressBuilder.of(customer.getCompleteAddress()).build();
        final Cart cartWithAddress = sphereClient().execute(CartUpdateCommand.of(updatedCart, SetShippingAddress.of(address))).toCompletableFuture().join();
        return cartWithAddress;
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
        final Optional<ProductVariant> variant = product.getAllVariants().stream()
                .filter(v -> v.getAttribute(ShopKeys.ID_MONTHLY).getValue(AttributeAccess.ofString()).equals(pactasId))
                .filter(v -> v.getAttribute(ShopKeys.ID_TWO_WEEKS).getValue(AttributeAccess.ofString()).equals(pactasId))
                .filter(v -> v.getAttribute(ShopKeys.ID_WEEKLY).getValue(AttributeAccess.ofString()).equals(pactasId))
                .findFirst();
        return variant;
    }
}
