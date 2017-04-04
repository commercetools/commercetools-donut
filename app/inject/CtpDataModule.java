package inject;

import com.google.inject.AbstractModule;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.types.Type;

import javax.inject.Singleton;

public class CtpDataModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TaxCategory.class).toProvider(TaxCategoryProvider.class).asEagerSingleton();
        bind(ProductType.class).toProvider(ProductTypeProvider.class).asEagerSingleton();
        bind(ProductProjection.class).toProvider(ProductProvider.class).in(Singleton.class);
        bind(Type.class).toProvider(CartTypeProvider.class).in(Singleton.class);
    }
}