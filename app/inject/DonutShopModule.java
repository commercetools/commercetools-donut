package inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.http.ApacheHttpClientAdapter;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Configuration;
import play.Environment;
import play.Logger;
import services.*;

import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

//import pactas.PactasImpl;

public class DonutShopModule extends AbstractModule {

    private static final Logger.ALogger LOG = Logger.of(DonutShopModule.class);

    private final Environment environment;
    private final Configuration configuration;

    public DonutShopModule(final Environment environment, final Configuration configuration) {
        this.environment = requireNonNull(environment, "'environment' must not be null");
        this.configuration = requireNonNull(configuration, "'configuration' must not be null");
    }

    @Override
    protected void configure() {
        bind(CartService.class).to(CartServiceImpl.class);
        bind(OrderService.class).to(OrderServiceImpl.class);
        bind(ProductService.class).to(ProductServiceImpl.class);
        bind(Pactas.class).to(PactasImpl.class);
    }

    @Provides
    @Singleton
    public SphereClient sphereClient() {
        final String projectKey = configuration.getString("sphere.project");
        final String clientId = configuration.getString("sphere.clientId");
        final String clientSecret = configuration.getString("sphere.clientSecret");
        final SphereClientFactory factory = SphereClientFactory.of(() -> ApacheHttpClientAdapter.of(HttpAsyncClients.createDefault()));
        final SphereClient sphereClient = factory.createClient(projectKey, clientId, clientSecret);
        LOG.debug("Created SphereClient");
        return sphereClient;
    }
}
