package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static play.test.Helpers.running;

public class ExporterServiceIntegrationTest {

    private Application application;
    private ExporterService exporterService;
    private PlayJavaSphereClient sphereClient;

    private static final long ALLOWED_TIMEOUT = 5000;


    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        exporterService = application.injector().instanceOf(ExporterService.class);
        sphereClient = application.injector().instanceOf(PlayJavaSphereClient.class);
    }

    //@Test
    public void testCreateCustomType() {
        running(application, () -> {
            final Type customType = exporterService.createCustomType().get(ALLOWED_TIMEOUT);
            assertThat(customType).isNotNull();
        });
    }

    //@Test
    public void deleteCustomType() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(type -> {
                Type execute = sphereClient.execute(TypeDeleteCommand.of(type)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + type.getKey());
            });
        });
    }

    //@Test
    public void deleteCarts() throws InterruptedException {
        final CartQuery query = CartQuery.of();
        List<Cart> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
        System.err.println("#### Before delete " + results.size());
        assertThat(results.isEmpty()).isFalse();
        results.forEach(cart -> {
            final Cart execute = sphereClient.execute(CartDeleteCommand.of(cart)).get(ALLOWED_TIMEOUT);
            System.err.println("#### Deleted Cart " + cart.getId());

        });
        Thread.sleep(10000);
        results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
        System.err.println("#### After delete " + results.size());
    }

    //@Test
    public void getCustomTypes() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            System.err.println(results);
            assertThat(results.isEmpty()).isFalse();
        });
    }

    @Test
    public void testGetProduct() {
        running(application, () -> {
            final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent();
            final F.Promise<PagedQueryResult<ProductProjection>> productProjectionPagedQueryResultPromise =
                    sphereClient.execute(query);

            final List<ProductProjection> results =
                    productProjectionPagedQueryResultPromise.get(ALLOWED_TIMEOUT, TimeUnit.MILLISECONDS).getResults();
            assertThat(results.isEmpty()).isFalse();

        });
    }
}

