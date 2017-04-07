import com.google.inject.AbstractModule;
import inject.SphereClientProvider;
import inject.ProductProvider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import pactas.Pactas;
import pactas.PactasImpl;

import javax.inject.Singleton;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(SphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
        bind(Pactas.class).to(PactasImpl.class).in(Singleton.class);
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
    }
}
