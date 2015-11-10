package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.VariantIdentifier;
import org.junit.Before;
import org.junit.Test;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static services.CartFixtures.withCart;
import static services.CartFixtures.withCartAndBox;
import static utils.JsonUtils.readObjectFromResource;

public class CartServiceIntegrationTest extends WithApplication {

    private static final long ALLOWED_TIMEOUT = 5000;
    private static final int FREQUENCY = 1;
    private static final PactasContract CONTRACT = readObjectFromResource("pactas-contract.json", PactasContract.class);
    private static final PactasCustomer CUSTOMER = readObjectFromResource("pactas-customer.json", PactasCustomer.class);

    private Application application;
    private CartService cartService;
    private ProductProjection productProjection;
    private PlayJavaSphereClient playJavaSphereClient;

    @Override
    protected Application provideApplication() {
        application = new GuiceApplicationBuilder().build();
        return application;
    }

    @Before
    public void setUp() {
        cartService = application.injector().instanceOf(CartService.class);
        productProjection = application.injector().instanceOf(ProductProjection.class);
        playJavaSphereClient = application.injector().instanceOf(PlayJavaSphereClient.class);
    }

    @Test
    public void testGetOrCreateCartWithEmptySession() {
        withCart(playJavaSphereClient, cart -> {
            final Cart newCart = cartService.getOrCreateCart(emptySession()).get(ALLOWED_TIMEOUT);
            assertThat(cart).isNotNull();
            assertThat(cart.getLineItems().isEmpty());
            return newCart;
        });
    }

    @Test
    public void testGetOrCreateCartWithExistingCart() {
        withCart(playJavaSphereClient, cart -> {
            final Cart existing = cartService.getOrCreateCart(sessionWithCartId(cart.getId())).get(ALLOWED_TIMEOUT);
            assertThat(existing).isNotNull();
            return existing;
        });
    }

    @Test
    public void testSetProductToCart() {
        withCart(playJavaSphereClient, cart -> {
            final Cart cartWithProduct = cartService.setProductToCart(cart, variantIdentifier(productProjection.getId(),
                    productProjection.getMasterVariant().getId()), FREQUENCY).get(ALLOWED_TIMEOUT);
            assertThat(cartWithProduct.getLineItems().size()).isEqualTo(FREQUENCY);
            return cartWithProduct;
        });
    }

    @Test
    public void testClearCart() {
        withCartAndBox(playJavaSphereClient, variantIdentifier(productProjection.getId(), FREQUENCY), cart -> {
            final Cart clearedCart = cartService.clearCart(cart).get(ALLOWED_TIMEOUT);
            assertThat(clearedCart.getLineItems().isEmpty()).isTrue();
            return clearedCart;
        });
    }

    @Test
    public void testCreateCartWithPactasInfo() {
        withCartAndBox(playJavaSphereClient, variantIdentifier(productProjection.getId(), FREQUENCY),
                cart -> {
                    final Cart cartWithPactasInfo = cartService.createCartWithPactasInfo(productProjection, CONTRACT,
                            CUSTOMER).get(ALLOWED_TIMEOUT);
                    assertThat(cartWithPactasInfo).isNotNull();
                    assertThat(cartWithPactasInfo.getLineItems().size()).isEqualTo(1);
                    assertThat(cartWithPactasInfo.getShippingAddress()).isNotNull();
                    return cartWithPactasInfo;
                });
    }

    private Http.Session emptySession() {
        return new Http.Session(new HashMap<>());
    }

    private Http.Session sessionWithCartId(final String cartId) {
        final Map<String, String> sessionMap = new HashMap<>();
        sessionMap.put(SessionKeys.CART_ID, cartId);
        return new Http.Session(sessionMap);
    }

    private static VariantIdentifier variantIdentifier(final String productId, final Integer variantId) {
        return VariantIdentifier.of(productId, variantId);
    }
}