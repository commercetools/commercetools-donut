package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import exceptions.PlanVariantNotFoundException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.PlayJavaSphereClient;
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
import play.libs.F;
import play.mvc.Http;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Singleton
public class CartServiceImpl extends AbstractShopService implements CartService {

    private static final Logger.ALogger LOG = Logger.of(CartServiceImpl.class);

    @Inject
    public CartServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Cart> getOrCreateCart(final Http.Session session) {
        requireNonNull(session);
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(cardId -> {
                    LOG.debug("Fetching existing Cart[cartId={}]", cardId);
                    return playJavaSphereClient().execute(CartByIdGet.of(cardId));
                })
                .orElseGet(() -> {
                    LOG.debug("Creating new Cart");
                    return playJavaSphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR)));
                });
    }


    @Override
    public F.Promise<Cart> clearCart(final Cart cart) {
        requireNonNull(cart);
        clearFrequency(cart.getId());
        final List<RemoveLineItem> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item, 1);
            return removeLineItem;
        }).collect(Collectors.toList());
        final F.Promise<Cart> clearedCartPromise = playJavaSphereClient().execute(CartUpdateCommand.of(cart, items));
        return clearedCartPromise.map(clearedCart -> {
            LOG.debug("Cleared Cart[cartId={}]", clearedCart.getId());
            return clearedCart;
        });
    }


    private void clearFrequency(final String cartId) {
        requireNonNull(cartId);
        final Optional<F.Promise<CustomObject<JsonNode>>> result = Optional.ofNullable(
                playJavaSphereClient().execute(CustomObjectByKeyGet.of(PactasKeys.FREQUENCY, cartId)));
        if (result.isPresent()) {
            playJavaSphereClient().execute(CustomObjectDeleteCommand.of(PactasKeys.FREQUENCY, cartId));
            LOG.debug("Cleared CustomObject");
        }
    }


    @Override
    public F.Promise<Cart> setProductToCart(final Cart cart, final ProductProjection product, final ProductVariant variant,
                                            final int frequency) {
        requireNonNull(cart);
        requireNonNull(product);
        requireNonNull(variant);

        final CustomObjectDraft<Integer> draft = CustomObjectDraft.ofUnversionedUpsert(SessionKeys.FREQUENCY, cart.getId(),
                frequency, new TypeReference<CustomObject<Integer>>() {
                });
        final F.Promise<CustomObject<Integer>> customObjectPromise =
                playJavaSphereClient().execute(CustomObjectUpsertCommand.of(draft));

        return customObjectPromise.flatMap(customObject -> {
            LOG.debug("Set CustomObject[container={}, key={}, value={}]", customObject.getContainer(), customObject.getKey(), customObject.getValue());
            return playJavaSphereClient().execute(CartUpdateCommand.of(cart, AddLineItem.of(product.getId(),
                    variant.getId(), frequency)));
        });
    }

    @Override
    public F.Promise<Cart> deleteCart(final Cart cart) {
        requireNonNull(cart);
        return playJavaSphereClient().execute(CartDeleteCommand.of(cart));
    }

    @Override
    public F.Promise<Integer> getFrequency(final String cartId) {
        requireNonNull(cartId);
        final F.Promise<CustomObject<JsonNode>> customObjectPromise =
                playJavaSphereClient().execute(CustomObjectByKeyGet.of(PactasKeys.FREQUENCY, cartId));
        return customObjectPromise.map(nullableCustomObject -> extractFrequency(nullableCustomObject));
    }

    private Integer extractFrequency(@Nullable final CustomObject<JsonNode> nullableCustomObject) {
        final int result = Optional.ofNullable(nullableCustomObject)
                .map(customObject -> customObject.getValue().asInt())
                .orElse(0);
        LOG.debug("Extracted frequency: {}", result);
        return result;
    }

    @Override
    public Optional<ProductVariant> getSelectedVariantFromCart(final Cart cart) {
        requireNonNull(cart);
        return (!cart.getLineItems().isEmpty()) ? Optional.ofNullable(cart.getLineItems().get(0).getVariant()) : Optional.empty();
    }

    @Override
    public F.Promise<Cart> createCartWithPactasInfo(final ProductProjection product, final PactasContract contract, final PactasCustomer customer) {
        requireNonNull(product);
        requireNonNull(contract);
        requireNonNull(customer);
        final F.Promise<Cart> createdCartPromise = playJavaSphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR)));
        return createdCartPromise.flatMap(createdCart -> {
            LOG.debug("Created new Cart with Pactas info[cartId={}] with Pactas info", createdCart.getId());
            final ProductVariant variant = getVariantInContract(product, contract);
            final AddLineItem action = AddLineItem.of(product, variant.getId(), 1);
            return playJavaSphereClient().execute(CartUpdateCommand.of(createdCart, action));
        }).flatMap(updatedCart -> {
            final Address address = AddressBuilder.of(customer.getCompleteAddress()).build();
            return playJavaSphereClient().execute(CartUpdateCommand.of(updatedCart, SetShippingAddress.of(address)));
        });
    }

    private ProductVariant getVariantInContract(final ProductProjection product, final PactasContract contract) {
        final String planVariantId = contract.getPlanVariantId();
        return variant(product, planVariantId).orElseThrow(() -> new PlanVariantNotFoundException(planVariantId));
    }

    private Optional<ProductVariant> variant(final ProductProjection product, final String pactasId) {
        requireNonNull(pactasId);
        return product.getAllVariants().stream().map(variant -> {
                    final String monthly = variant.getAttribute(PactasKeys.ID_MONTHLY).getValue(AttributeAccess.ofString());
                    final String twoWeeks = variant.getAttribute(PactasKeys.ID_TWO_WEEKS).getValue(AttributeAccess.ofString());
                    final String weekly = variant.getAttribute(PactasKeys.ID_WEEKLY).getValue(AttributeAccess.ofString());
                    return (pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) ? variant : null;
                }
        ).findFirst();
    }
}