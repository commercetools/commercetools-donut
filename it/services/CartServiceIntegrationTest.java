package services;


import controllers.ProductController;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.VariantIdentifier;
import io.sphere.sdk.types.CustomFields;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import play.api.mvc.RequestHeader;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.test.FakeApplication;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.start;

public class CartServiceIntegrationTest {

    private static FakeApplication fakeApplication;
    private final static Request request = mock(Request.class);

    private ProductController productController;
    private CartService cartService;
    private ProductProjection productProjection;

    private static final long ALLOWED_TIMEOUT = 5000;
    private static final int FREQUENCY = 1;

    @BeforeClass
    public static void startFakeApplication() {
        fakeApplication = fakeApplication();
        start(fakeApplication);
    }

    @Before
    /** Set up the context necessary for a contoller to run **/
    public void setUpContext() {
        final Map<String, String> flashData = Collections.emptyMap();
        final Map<String, Object> argData = Collections.emptyMap();
        final RequestHeader header = mock(RequestHeader.class);
        final Http.Context context = new Http.Context(1L, header, request, flashData, flashData, argData);
        Http.Context.current.set(context);

        productController = fakeApplication.injector().instanceOf(ProductController.class);
        cartService = fakeApplication.injector().instanceOf(CartService.class);
        productProjection = fakeApplication.injector().instanceOf(ProductProjection.class);
    }

    private static VariantIdentifier variantIdentifier(final String productId, final Integer variantId) {
        return VariantIdentifier.of(productId, variantId);
    }

    @Ignore
    @Test
    public void testGetOrCreateCart() {
        final Cart cart = cartService.getOrCreateCart(productController.session()).get(ALLOWED_TIMEOUT);
        assertThat(cart).isNotNull();
        final CustomFields customFields = cart.getCustom();
        assertThat(customFields).isNotNull();
        final Integer frequency = customFields.getFieldAsInteger("frequency");
        assertThat(frequency).isNotNull();
        assertThat(frequency).isEqualTo(0);
    }

    @Ignore
    @Test
    public void testSetProductToCart() {
        final Cart cart = cartService.getOrCreateCart(productController.session()).get(ALLOWED_TIMEOUT);
        final Cart cartWithProduct = cartService.setProductToCart(cart, variantIdentifier(productProjection.getId(),
                productProjection.getMasterVariant().getId()), FREQUENCY).get(ALLOWED_TIMEOUT);
        assertThat(cartWithProduct.getLineItems().size()).isEqualTo(1);
    }

    @Ignore
    @Test
    public void testClearCart() {
        final Cart cart = cartService.getOrCreateCart(productController.session()).get(ALLOWED_TIMEOUT);
        final Cart cartWithProduct = cartService.setProductToCart(cart, variantIdentifier(productProjection.getId(),
                productProjection.getMasterVariant().getId()), FREQUENCY).get(ALLOWED_TIMEOUT);
        final Cart clearedCart = cartService.clearCart(cartWithProduct).get(ALLOWED_TIMEOUT);
        assertThat(clearedCart.getLineItems()).isEmpty();
    }

    @Ignore
    @Test
    public void testGetFrequency() {
        final Cart cart = cartService.getOrCreateCart(productController.session()).get(ALLOWED_TIMEOUT);
        final Cart cartWithProduct = cartService.setProductToCart(cart, variantIdentifier(productProjection.getId(),
                productProjection.getMasterVariant().getId()), FREQUENCY).get(ALLOWED_TIMEOUT);
        final F.Promise<Integer> result = cartService.getFrequency(cartWithProduct.getId());
        assertThat(result.get(ALLOWED_TIMEOUT)).isNotNull();
        assertThat(result.get(ALLOWED_TIMEOUT)).isEqualTo(FREQUENCY);
    }

    @Ignore
    @Test
    public void testGetSelectedVariant() {
        //TODO
    }

    @Ignore
    @Test
    public void testCreateCartWithPactasInfo() {
        //TODO
    }
}