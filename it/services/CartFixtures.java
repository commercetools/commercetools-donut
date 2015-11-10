package services;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetCustomField;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.VariantIdentifier;
import io.sphere.sdk.types.CustomFieldsDraft;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import models.wrapper.TypeDraftWrapper;
import utils.JsonUtils;

import javax.money.CurrencyUnit;
import java.util.*;
import java.util.function.UnaryOperator;


public class CartFixtures {

    private static final CountryCode DEFAULT_COUNTRY = CountryCode.DE;
    private static final CurrencyUnit DEFAULT_CURRENCY = DefaultCurrencyUnits.EUR;

    private static final String TYPE_DRAFT_JSON_RESOURCE = "data/type-draft.json";
    private static final String FREQUENCY_TYPE_KEY = "cart-frequency-key";
    private static final String FREQUENCY_FIELD_KEY = "frequency";
    private static final int ALLOWED_TIMEOUT = 5000;

    public static Cart createCart(final PlayJavaSphereClient client, final CartDraft cartDraft) {
        return client.execute(CartCreateCommand.of(cartDraft)).get(ALLOWED_TIMEOUT);
    }

    public static Cart createCartFromDraft(final PlayJavaSphereClient client) {
        final CartDraft cartDraft = CartDraft.of(DEFAULT_CURRENCY).withCountry(DEFAULT_COUNTRY)
                .witCustom(CustomFieldsDraft.ofTypeKeyAndObjects(FREQUENCY_TYPE_KEY, frequencyType(0)));
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
        final List<? extends UpdateAction<Cart>> cartUpdateActions = Arrays.asList(
                SetCustomField.ofObject(FREQUENCY_FIELD_KEY, 1),
                AddLineItem.of(variantIdentifier.getProductId(), variantIdentifier.getVariantId(), 1)
        );
        final Cart cartWithProduct = client.execute(CartUpdateCommand.of(cart, cartUpdateActions)).get(ALLOWED_TIMEOUT);
        final Cart cartToDelete = operator.apply(cartWithProduct);
        client.execute(CartDeleteCommand.of(cartToDelete));
    }

    public static Type createCustomType(final PlayJavaSphereClient client) {
        final TypeDraftWrapper typeDraftWrapper = JsonUtils.readObjectFromResource(TYPE_DRAFT_JSON_RESOURCE,
                TypeDraftWrapper.class);
        final TypeDraft typeDraft = typeDraftWrapper.createTypeDraft();
        return client.execute(TypeCreateCommand.of(typeDraft)).get(ALLOWED_TIMEOUT);
    }

    public static void withCustomType(final PlayJavaSphereClient client, final UnaryOperator<Type> operator) {
        final Type customType = createCustomType(client);
        final Type customTypeToDelete = operator.apply(customType);
        client.execute(TypeDeleteCommand.of(customTypeToDelete)).get(ALLOWED_TIMEOUT);
    }

    private static Map<String, Object> frequencyType(final int frequency) {
        return Collections.unmodifiableMap(new HashMap<String, Integer>() {
            {
                put(FREQUENCY_FIELD_KEY, frequency);
            }
        });
    }
}
