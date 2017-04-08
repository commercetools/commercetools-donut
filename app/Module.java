import com.google.inject.AbstractModule;
import donut.inject.ProductProvider;
import donut.inject.SphereClientProvider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;

import javax.inject.Singleton;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(SphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
    }
}
