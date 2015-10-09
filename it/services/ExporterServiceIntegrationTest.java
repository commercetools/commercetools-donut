package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static play.test.Helpers.running;

public class ExporterServiceIntegrationTest {

    private Application application;
    private ExportService exportService;
    private PlayJavaSphereClient sphereClient;

    private static final long ALLOWED_TIMEOUT = 5000;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        exportService = application.injector().instanceOf(ExportService.class);
        sphereClient = application.injector().instanceOf(PlayJavaSphereClient.class);
    }


    @Ignore
    @Test
    public void testCreateProductType() {
        final Product productType = exportService.createProductModel().get(ALLOWED_TIMEOUT);

    }

    @Ignore
    @Test
    public void getCustomTypes() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            System.err.println(results);
            assertThat(results.isEmpty()).isFalse();
        });
    }

    @Ignore
    @Test
    public void testCreateCustomType() {
        running(application, () -> {
            final Type customType = exportService.createCustomType().get(ALLOWED_TIMEOUT);
            assertThat(customType).isNotNull();
        });
    }

    @Ignore
    @Test
    public void deleteCustomType() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(type -> {
                final Type execute = sphereClient.execute(TypeDeleteCommand.of(type)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + type.getKey());
            });
        });
    }

    @Ignore
    @Test
    public void deleteCarts() throws InterruptedException {
        final CartQuery query = CartQuery.of();
        PagedQueryResult<Cart> cartPagedQueryResult = sphereClient.execute(query).get(ALLOWED_TIMEOUT);
        System.err.println("#### Before delete " + cartPagedQueryResult.getTotal());
        assertThat(cartPagedQueryResult.getResults().isEmpty()).isFalse();
        cartPagedQueryResult.getResults().forEach(cart -> {
            final Cart execute = sphereClient.execute(CartDeleteCommand.of(cart)).get(ALLOWED_TIMEOUT);
            System.err.println("#### Deleted Cart " + cart.getId());
        });
        cartPagedQueryResult = sphereClient.execute(query).get(ALLOWED_TIMEOUT);
        System.err.println("#### After delete " + cartPagedQueryResult.getTotal());
    }
}

