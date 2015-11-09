package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.VariantIdentifier;
import io.sphere.sdk.types.CustomFields;
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
import static services.CartFixtures.*;
import static utils.JsonUtils.readObjectFromResource;

public class CartServiceIntegrationTest extends WithApplication {

    private static final long ALLOWED_TIMEOUT = 5000;

    private Application application;
    private CartService cartService;
    private ProductProjection productProjection;
    private PlayJavaSphereClient playJavaSphereClient;

    private static final String FREQUENCY_FIELD_KEY = "frequency";

    public static final PactasContract CONTRACT = readObjectFromResource("pactas-contract.json", PactasContract.class);
    public static final PactasCustomer CUSTOMER = readObjectFromResource("pactas-customer.json", PactasCustomer.class);

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
        final Cart cart = cartService.getOrCreateCart(emptySession()).get(ALLOWED_TIMEOUT);
        assertThat(cart).isNotNull();
        assertThat(cart.getLineItems().isEmpty());
        final CustomFields customFields = cart.getCustom();
        assertThat(customFields).isNotNull();
        final Integer frequency = customFields.getFieldAsInteger(FREQUENCY_FIELD_KEY);
        assertThat(frequency).isEqualTo(0);
    }

    @Test
    public void testGetOrCreateCartWithExistingCart() {
        withCustomType(playJavaSphereClient, type -> {
            withCart(playJavaSphereClient, cart -> {
                final Cart existing = cartService.getOrCreateCart(sessionWithCartId(cart.getId())).get(5000);
                assertThat(existing).isNotNull();
                return existing;
            });
            return type;
        });
    }

    @Test
    public void testSetProductToCart() {
        withCustomType(playJavaSphereClient, type -> {
            withCart(playJavaSphereClient, cart -> {
                final Cart cartWithProduct = cartService.setProductToCart(cart, variantIdentifier(productProjection.getId(),
                        productProjection.getMasterVariant().getId()), 1).get(ALLOWED_TIMEOUT);
                assertThat(cartWithProduct.getLineItems().size()).isEqualTo(1);
                return cartWithProduct;
            });
            return type;
        });
    }


    @Test
    public void testClearCart() {
        withCustomType(playJavaSphereClient, type -> {
            final VariantIdentifier variantIdentifier = VariantIdentifier.of(productProjection.getId(), 1);
            withCartAndBox(playJavaSphereClient, variantIdentifier, cart -> {
                final Cart clearedCart = cartService.clearCart(cart).get(5000);
                assertThat(clearedCart.getLineItems().isEmpty()).isTrue();
                return clearedCart;
            });
            return type;
        });
    }

    @Test
    public void testCreateCartWithPactasInfo() {
        //TODO
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