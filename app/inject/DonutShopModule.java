package inject;

import com.google.inject.AbstractModule;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import pactas.Pactas;
import pactas.PactasImpl;
import services.*;

import javax.inject.Singleton;

public class DonutShopModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
        bind(PlayJavaSphereClient.class).toProvider(PlayJavaSphereClientProvider.class).in(Singleton.class);
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
        bind(CartService.class).to(CartServiceImpl.class).in(Singleton.class);
        bind(OrderService.class).to(OrderServiceImpl.class).in(Singleton.class);
        bind(ProductService.class).to(ProductServiceImpl.class).in(Singleton.class);
        bind(Pactas.class).to(PactasImpl.class).in(Singleton.class);

    }
}
