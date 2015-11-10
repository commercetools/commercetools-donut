package services;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.VariantIdentifier;

import javax.money.CurrencyUnit;
import java.util.function.UnaryOperator;


public class CartFixtures {

    private static final CountryCode DEFAULT_COUNTRY = CountryCode.DE;
    private static final CurrencyUnit DEFAULT_CURRENCY = DefaultCurrencyUnits.EUR;
    private static final int ALLOWED_TIMEOUT = 5000;

    public static Cart createCart(final PlayJavaSphereClient client, final CartDraft cartDraft) {
        return client.execute(CartCreateCommand.of(cartDraft)).get(ALLOWED_TIMEOUT);
    }

    public static Cart createCartFromDraft(final PlayJavaSphereClient client) {
        final CartDraft cartDraft = CartDraft.of(DEFAULT_CURRENCY).withCountry(DEFAULT_COUNTRY);
        return createCart(client, cartDraft);
    }

    public static void withCart(final PlayJavaSphereClient client, final UnaryOperator<Cart> operator) {
        final Cart cart = createCartFromDraft(client);
        final Cart cartToDelete = operator.apply(cart);
        client.execute(CartDeleteCommand.of(cartToDelete));
    }

    public static void withCartAndBox(final PlayJavaSphereClient client, final VariantIdentifier variantIdentifier,
                                      final UnaryOperator<Cart> operator) {
        final Cart cart = createCartFromDraft(client);
        final Cart cartWithProduct = client.execute(CartUpdateCommand.of(cart,
                AddLineItem.of(variantIdentifier.getProductId(), variantIdentifier.getVariantId(), 1)))
                .get(ALLOWED_TIMEOUT);
        final Cart cartToDelete = operator.apply(cartWithProduct);
        client.execute(CartDeleteCommand.of(cartToDelete));
    }
}
