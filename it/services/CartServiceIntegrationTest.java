package services;


import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.types.CustomFields;
import org.junit.Test;
import play.mvc.Http;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static services.TestFixtures.deleteCartWithRetry;

public class CartServiceIntegrationTest extends WithSphereClient {

    private static final int FREQUENCY = 1;

    @Test
    public void testSetProductToCart() throws Exception {
        final CartService cartService = new PactasWebHookActionControllerImpl(sphereClient, cartType);
        final Cart cart = cartService.getOrCreateCart(emptySession()).toCompletableFuture().get();
        final CustomFields customFields = cart.getCustom();
        assertThat(customFields).isNotNull();
        assertThat(customFields.getFieldAsInteger("frequency")).isEqualTo(0);
        assertThat(cart.getLineItems()).isEmpty();
        final Cart cartWithProduct = cartService.setProductToCart(cart, product.getMasterVariant().getIdentifier(), FREQUENCY)
                .toCompletableFuture().join();
        assertThat(cartWithProduct.getLineItems()).hasSize(1);
        assertThat(cartService.getFrequency(cartWithProduct)).contains(FREQUENCY);
        final Cart clearedCart = cartService.clearCart(cartWithProduct).toCompletableFuture().join();
        assertThat(clearedCart.getLineItems()).isEmpty();
        deleteCartWithRetry(sphereClient, clearedCart);
    }

    private static Http.Session emptySession() {
        return new Http.Session(emptyMap());
    }
}