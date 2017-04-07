package pactas;

import com.google.inject.AbstractModule;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.products.ProductProjection;
import org.junit.Test;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PactasIntegrationTest extends WithApplication {

    private static final String CUSTOMER_ID = "58e3a4af14aa010f3864eda0";
    private static final String CONTRACT_ID = "58e3a4af14aa010f3864eda1";
    private static final ProductProjection PRODUCT = readObjectFromResource("product.json", ProductProjection.class);

    private final BlockingSphereClient sphereClient = mock(BlockingSphereClient.class);

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ProductProjection.class).toInstance(PRODUCT);
                        bind(BlockingSphereClient.class).toInstance(sphereClient);
                    }
                }).build();
    }

    @Test
    public void fetchesContractAndCustomer() throws Exception {
        final Pactas pactas = app.injector().instanceOf(Pactas.class);
        final PactasContract contract = pactas.fetchContract(CONTRACT_ID).toCompletableFuture().get();
        assertThat(contract.getId()).isEqualTo(CONTRACT_ID);
        final PactasCustomer customer = pactas.fetchCustomer(CUSTOMER_ID).toCompletableFuture().get();
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
    }
}
