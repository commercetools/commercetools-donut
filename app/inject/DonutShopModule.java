package inject;

import com.google.inject.AbstractModule;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.client.SphereClient;
import pactas.Pactas;
import pactas.PactasImpl;
import play.Configuration;
import play.Environment;
import play.Logger;
import services.*;

import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

public class DonutShopModule extends AbstractModule {

    private static final Logger.ALogger LOG = Logger.of(DonutShopModule.class);

    private final Environment environment;
    private final Configuration configuration;

    public DonutShopModule(final Environment environment, final Configuration configuration) {
        this.environment = requireNonNull(environment);
        this.configuration = requireNonNull(configuration);
    }

    @Override
    protected void configure() {
        bind(SphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
        bind(PlayJavaSphereClient.class).toProvider(PlayJavaSphereClientProvider.class).in(Singleton.class);
        bind(CartService.class).to(CartServiceImpl.class).in(Singleton.class);
        bind(OrderService.class).to(OrderServiceImpl.class).in(Singleton.class);
        bind(ProductService.class).to(ProductServiceImpl.class).in(Singleton.class);
        bind(Pactas.class).to(PactasImpl.class).in(Singleton.class);
    }
}
