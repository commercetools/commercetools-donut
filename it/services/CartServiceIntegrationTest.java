package services;


import controllers.ProductController;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.api.mvc.RequestHeader;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.test.FakeApplication;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static play.test.Helpers.*;

public class CartServiceIntegrationTest {

    private static FakeApplication fakeApplication;
    private final static Request request = mock(Request.class);

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
    }

    @Test
    public void testGetOrCreateCartSessionIdIsStored() {
        final ProductController controller = fakeApplication.injector().instanceOf(ProductController.class);
        final Result result = controller.show();
        assertThat(result.status()).isEqualTo(OK);
        assertThat(controller.session().get(SessionKeys.CART_ID)).isNotEmpty();
    }

    @Test
    public void testClearCart() {
        //TODO
    }

    @Test
    public void testSetProductToCart() {
        //TODO
    }

    @Test
    public void testGetFrequency() {
        //TODO
    }

    @Test
    public void testGetSelectedVariant() {
        //TODO
    }

    @Test
    public void testCreateCartWithPactasInfo() {
        //TODO
    }
}
