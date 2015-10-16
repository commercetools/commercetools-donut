package inject;

import com.google.inject.AbstractModule;
import io.sphere.sdk.products.ProductProjection;

import javax.inject.Singleton;

public class ProductModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
    }
}