package services;


import controllers.ProductController;
import exceptions.ProductNotFoundException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.api.mvc.RequestHeader;
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
    private ProductService productService;

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
        //Http.Context.current().session().put(SessionKeys.CART_ID, "600006");

        productController = fakeApplication.injector().instanceOf(ProductController.class);
        cartService = fakeApplication.injector().instanceOf(CartService.class);
        productService = fakeApplication.injector().instanceOf(ProductService.class);
    }

    @Test
    public void _testGetOrCreateCart() {
        final Cart cart = cartService._getOrCreateCart(productController.session()).get(2000);
        assertThat(cart.getId()).isNotNull();
    }

    @Test
    public void _testSetProductToCart() {
        final Cart cart = cartService._getOrCreateCart(productController.session()).get(2000);
        final ProductProjection product = productService._getProduct().get(2000).orElseThrow(ProductNotFoundException::new);
        final Cart cartWithProduct = cartService._setProductToCart(cart, product, product.getMasterVariant(), 1).get(2000);
        assertThat(cartWithProduct.getLineItems().size()).isEqualTo(1);
    }

    @Test
    public void _testClearCart() {
        final Cart cart = cartService._getOrCreateCart(productController.session()).get(2000);
        final ProductProjection product = productService._getProduct().get(2000).orElseThrow(ProductNotFoundException::new);
        final Cart cartWithProduct = cartService._setProductToCart(cart, product, product.getMasterVariant(), 1).get(2000);
        final Cart clearedCart = cartService._clearCart(cartWithProduct).get(2000);
        assertThat(clearedCart.getLineItems()).isEmpty();
    }

    @Test
    public void testGetFrequency() {
        final Cart cart = cartService.getOrCreateCart(productController.session());
        final ProductProjection product = productService.getProduct().orElseThrow(ProductNotFoundException::new);
        cartService.setProductToCart(cart, product, product.getMasterVariant(), 1);
        final int result = cartService.getFrequency(cart.getId());
        assertThat(result).isEqualTo(1);
    }

    //TODO: NullPointerException
//    @Test
//    public void _testGetFrequency() {
//        final Cart cart = cartService._getOrCreateCart(productController.session()).get(2000);
//        final ProductProjection product = productService._getProduct().get(2000).orElseThrow(ProductNotFoundException::new);
//        final Cart cartWithProduct = cartService._setProductToCart(cart, product, product.getMasterVariant(), 1).get(2000);
//        final F.Promise<Integer> result = cartService._getFrequency(cartWithProduct.getId());
//        assertThat(result).isNotNull();
//        assertThat(result.get(2000)).isNotNull();
//        assertThat(result.get(2000)).isEqualTo(1);
//    }

    @Test
    public void testGetSelectedVariant() {
        //TODO
    }

    @Test
    public void testCreateCartWithPactasInfo() {
        //TODO
    }
}