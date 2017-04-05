package services;

import com.google.inject.Singleton;
import exceptions.PlanVariantNotFoundException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.LineItem;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetCustomField;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ByIdVariantIdentifier;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.Attribute;
import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.types.CustomFieldsDraft;
import io.sphere.sdk.types.Type;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Logger;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

@Singleton
public class CartServiceImpl extends AbstractShopService implements CartService {

    private static final Logger.ALogger LOG = Logger.of(CartServiceImpl.class);
    private static final String FREQUENCY_FIELD_KEY = "frequency";
    private final Type cartType;

    @Inject
    public CartServiceImpl(final SphereClient sphereClient, final Type cartType) {
        super(sphereClient);
        this.cartType = cartType;
    }

    @Override
    public CompletionStage<Cart> getOrCreateCart(final Http.Session session) {
        requireNonNull(session);
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(cardId -> sphereClient().execute(CartByIdGet.of(cardId)).thenApply(cart -> {
                    LOG.debug("Fetched existing Cart[cartId={}, items={}, custom={}]",
                            cart.getId(), cart.getLineItems().size(), cart.getCustom());
                    return cart;
                }))
                .orElseGet(() -> {
                    final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR)
                            .withCustom(CustomFieldsDraft.ofTypeIdAndObjects(cartType.getId(), singletonMap(FREQUENCY_FIELD_KEY, 0)));
                    return sphereClient().execute(CartCreateCommand.of(cartDraft))
                            .thenApply(cart -> {
                                LOG.debug("Created new Cart[cartId={}, items={}, custom={}]",
                                        cart.getId(), cart.getLineItems().size(), cart.getCustom());
                                return cart;
                            });
                });
    }

    @Override
    public CompletionStage<Cart> clearCart(final Cart cart) {
        final List<UpdateAction<Cart>> updateActions = cart.getLineItems().stream()
                .map(RemoveLineItem::of)
                .collect(Collectors.toList());
        updateActions.add(SetCustomField.ofObject(FREQUENCY_FIELD_KEY, 0));
        return sphereClient().execute(CartUpdateCommand.of(cart, updateActions))
                .whenComplete((clearedCart, throwable) -> LOG.debug("Cleared Cart: items={}, custom={}", clearedCart.getLineItems(), clearedCart.getCustom()));
    }

    @Override
    public CompletionStage<Cart> setProductToCart(final Cart cart, final ByIdVariantIdentifier variantIdentifier, int frequency) {
        requireNonNull(variantIdentifier);
        final List<UpdateAction<Cart>> cartUpdateActions = Arrays.asList(
                SetCustomField.ofObject(FREQUENCY_FIELD_KEY, frequency),
                AddLineItem.of(variantIdentifier.getProductId(), variantIdentifier.getVariantId(), 1));
        return sphereClient().execute(CartUpdateCommand.of(cart, cartUpdateActions))
                .whenComplete((updatedCart, throwable) -> LOG.debug("Updated Cart: items={}, custom={}", updatedCart.getLineItems(), updatedCart.getCustom()));
    }

    @Override
    public CompletionStage<Cart> deleteCart(final Cart cart) {
        return sphereClient().execute(CartDeleteCommand.of(cart));
    }

    @Override
    public Optional<Integer> getFrequency(final Cart cart) {
        return Optional.ofNullable(cart.getCustom())
                .flatMap(customFields -> Optional.ofNullable(customFields.getFieldAsInteger(FREQUENCY_FIELD_KEY)));
    }

    @Override
    public Optional<ProductVariant> getSelectedVariantFromCart(final Cart cart) {
        return cart.getLineItems().stream()
                .findFirst()
                .map(LineItem::getVariant);
    }

    @Override
    public CompletionStage<Cart> createCartWithPactasInfo(final ProductProjection product, final PactasContract contract, final PactasCustomer customer) {
        requireNonNull(product);
        requireNonNull(contract);
        requireNonNull(customer);
        final CompletionStage<Cart> createdCartPromise = sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR)));
        return createdCartPromise.thenCompose(createdCart -> {
            LOG.debug("Created new Cart with Pactas info[cartId={}] with Pactas info", createdCart.getId());
            final ProductVariant variant = getVariantInContract(product, contract);
            final AddLineItem action = AddLineItem.of(product, variant.getId(), 1);
            return sphereClient().execute(CartUpdateCommand.of(createdCart, action));
        }).thenCompose(updatedCart -> {
            final Address address = AddressBuilder.of(customer.getCompleteAddress()).build();
            return sphereClient().execute(CartUpdateCommand.of(updatedCart, SetShippingAddress.of(address)));
        });
    }

    private ProductVariant getVariantInContract(final ProductProjection product, final PactasContract contract) {
        final String planVariantId = contract.getPlanVariantId();
        return variant(product, planVariantId).orElseThrow(() -> new PlanVariantNotFoundException(planVariantId));
    }

    private Optional<ProductVariant> variant(final ProductProjection product, final String pactasId) {
        requireNonNull(pactasId);
        return product.getAllVariants().stream().map(variant -> {

                    final String monthly = Optional.<Attribute>ofNullable(variant.getAttribute(PactasKeys.ID_MONTHLY))
                            .map(attribute -> attribute.getValue(AttributeAccess.ofString()))
                            .orElseThrow(() -> new RuntimeException(format("Unable to get Attribute '%s'",
                                    PactasKeys.ID_MONTHLY)));

                    final String twoWeeks = Optional.<Attribute>ofNullable(variant.getAttribute(PactasKeys.ID_TWO_WEEKS))
                            .map(attribute -> attribute.getValue(AttributeAccess.ofString()))
                            .orElseThrow(() -> new RuntimeException(format("Unable to get Attribute '%s'",
                                    PactasKeys.ID_TWO_WEEKS)));

                    final String weekly = Optional.<Attribute>ofNullable(variant.getAttribute(PactasKeys.ID_WEEKLY))
                            .map(attribute -> attribute.getValue(AttributeAccess.ofString()))
                            .orElseThrow(() -> new RuntimeException(format("Unable to get Attribute '%s'",
                                    PactasKeys.ID_WEEKLY)));

                    return (pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) ? variant : null;
                }
        ).findFirst();
    }
}