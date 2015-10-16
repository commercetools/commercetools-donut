package services;

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
import io.sphere.sdk.carts.commands.updateactions.SetCustomField;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.queries.CustomObjectByKeyGet;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.types.CustomFieldsDraft;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Logger;
import play.libs.F;
import play.mvc.Http;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Singleton
public class CartServiceImpl extends AbstractShopService implements CartService {

    private static final Logger.ALogger LOG = Logger.of(CartServiceImpl.class);
    private static final String TYPE_KEY = "cart-frequency-key";
    private static final String FIELD_KEY = "frequency";

    @Inject
    public CartServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Cart> getOrCreateCart(final Http.Session session) {
        requireNonNull(session);
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(cardId -> {
                    return playJavaSphereClient().execute(CartByIdGet.of(cardId)).map(cart -> {
                        LOG.debug("Fetched existing Cart[cartId={}, items={}, custom frequency={}]", cart.getId(),
                                cart.getLineItems().size(), cart.getCustom().getFieldAsString("frequency"));
                        return cart;
                    });
                })
                .orElseGet(() -> {
                    final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR)
                            .witCustom(CustomFieldsDraft.ofTypeKeyAndObjects(TYPE_KEY, frequencyType(0)));
                    return playJavaSphereClient().execute(CartCreateCommand.of(cartDraft))
                            .map(cart -> {
                                LOG.debug("Created new Cart[cartId={}, items={}, custom frequency={}]", cart.getId(),
                                        cart.getLineItems().size(), cart.getCustom().getFieldAsString("frequency"));
                                return cart;
                            });
                });
    }

    private final static Map<String, Object> frequencyType(final int frequency) {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put(FIELD_KEY, String.valueOf(frequency)); //TODO number type
            }
        });
    }

    @Override
    public F.Promise<Cart> clearCart(final Cart cart) {
        requireNonNull(cart);

        final List<? extends UpdateAction<Cart>> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item);
            return removeLineItem;
        }).collect(Collectors.toList());

        final F.Promise<Cart> clearedItemsPromise = playJavaSphereClient().execute(CartUpdateCommand.of(cart, items));
        return clearedItemsPromise.flatMap(clearedItemsCart ->
                playJavaSphereClient().execute(CartUpdateCommand.of(clearedItemsCart,
                        SetCustomField.ofObject(FIELD_KEY, String.valueOf(0))))
                        .map(clearedTypeCart -> {
                            LOG.debug("Cleared Cart: items={}, custom frequency={}", clearedTypeCart.getLineItems().size(),
                                    clearedTypeCart.getCustom().getFieldAsString("frequency"));
                            return clearedTypeCart;
                        }));
    }

    @Override //TODO variant identifier, Integer
    public F.Promise<Cart> setProductToCart(final Cart cart, final ProductProjection product,
                                            final ProductVariant variant, final Integer frequency) {
        requireNonNull(cart);
        requireNonNull(product);
        requireNonNull(variant);
        requireNonNull(frequency);

        final List<? extends UpdateAction<Cart>> cartUpdateActions = Arrays.asList(
                SetCustomField.ofObject(FIELD_KEY, String.valueOf(frequency)),
                AddLineItem.of(product.getId(), variant.getId(), frequency)
        );

        return playJavaSphereClient().execute(CartUpdateCommand.of(cart, cartUpdateActions)).map(updatedCart -> {
            LOG.debug("Updated Cart: items={}, custom frequency={}", updatedCart.getLineItems().size(),
                    updatedCart.getCustom().getFieldAsString("frequency"));
            return cart;
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