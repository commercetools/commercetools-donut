import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import inject.BlockingSphereClientProvider;
import inject.CartTypeProvider;
import inject.ProductProvider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.types.Type;
import pactas.Pactas;
import pactas.PactasImpl;

import javax.inject.Singleton;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(BlockingSphereClient.class).toProvider(BlockingSphereClientProvider.class).in(Singleton.class);
        bind(Pactas.class).to(PactasImpl.class).in(Singleton.class);
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
        bind(Type.class).toProvider(CartTypeProvider.class).in(Singleton.class);
    }

    @Provides
    SphereClient provideSphereClient(final BlockingSphereClient blockingSphereClient) {
        return blockingSphereClient;
    }
}
