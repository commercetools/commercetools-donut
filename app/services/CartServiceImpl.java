package services;

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
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.VariantIdentifier;
import io.sphere.sdk.products.attributes.Attribute;
import io.sphere.sdk.products.attributes.AttributeAccess;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Logger;
import play.libs.F;
import play.mvc.Http;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Singleton
public class CartServiceImpl extends AbstractShopService implements CartService {

    private static final Logger.ALogger LOG = Logger.of(CartServiceImpl.class);
    private static final String FREQUENCY_TYPE_KEY = "cart-frequency-key";
    private static final String FREQUENCY_FIELD_KEY = "frequency";

    @Inject
    public CartServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Cart> getOrCreateCart(final Http.Session session) {
        requireNonNull(session);
        return Optional.ofNullable(session.get(SessionKeys.CART_ID))
                .map(cardId -> playJavaSphereClient().execute(CartByIdGet.of(cardId)).map(cart -> {
                    LOG.debug("Fetched existing Cart[cartId={}, items={}, frequency={}]",
                            cart.getId(), cart.getLineItems().size(), getFrequencyString(cart));
                    return cart;
                }))
                .orElseGet(() -> {
                    final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR);
                    return playJavaSphereClient().execute(CartCreateCommand.of(cartDraft))
                            .map(cart -> {
                                LOG.debug("Created new Cart[cartId={}, items={}, frequency={}]",
                                        cart.getId(), cart.getLineItems().size(), getFrequencyString(cart));
                                return cart;
                            });
                });
    }

    private String getFrequencyString(final Cart cart) {
        final long frequency = !cart.getLineItems().isEmpty() ? cart.getLineItems().get(0).getQuantity() : 0;
        return String.valueOf(frequency);
    }

    @Override
    public F.Promise<Cart> clearCart(final Cart cart) {
        requireNonNull(cart);
        final List<? extends UpdateAction<Cart>> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item);
            return removeLineItem;
        }).collect(Collectors.toList());
        return playJavaSphereClient().execute(CartUpdateCommand.of(cart, items));
    }

    @Override
    public F.Promise<Cart> setProductToCart(final Cart cart, final VariantIdentifier variantIdentifier, int frequency) {
        requireNonNull(cart);
        requireNonNull(variantIdentifier);
        requireNonNull(frequency);
        return playJavaSphereClient().execute(CartUpdateCommand.of(cart,
                AddLineItem.of(variantIdentifier.getProductId(), variantIdentifier.getVariantId(), frequency)));
    }

    @Override
    public F.Promise<Cart> deleteCart(final Cart cart) {
        requireNonNull(cart);
        return playJavaSphereClient().execute(CartDeleteCommand.of(cart));
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