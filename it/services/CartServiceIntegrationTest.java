package services;


import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.types.CustomFields;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static services.TestFixtures.deleteCartWithRetry;

public class CartServiceIntegrationTest extends WithSphereClient {

    private static final int FREQUENCY = 1;

    private CartService cartService;
    private ProductProjection product;

    @Before
    public void setUp() throws Exception {
        this.cartService = app.injector().instanceOf(CartService.class);
        this.product = app.injector().instanceOf(ProductProjection.class);
    }

    @Test
    public void testSetProductToCart() throws Exception {
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