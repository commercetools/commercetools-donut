package services;

import com.google.inject.AbstractModule;
import inject.CartTypeProvider;
import inject.ProductProvider;
import inject.ProductTypeProvider;
import inject.TaxCategoryProvider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.types.Type;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

public abstract class WithSphereClient extends WithApplication {

    protected static TestableSphereClient sphereClient;
    protected static ProductProjection product;
    protected static Type cartType;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(BlockingSphereClient.class).toInstance(sphereClient);
                    }
                }).build();
    }

    protected static TestableSphereClient provideSphereClient() {
        return new TestableSphereClient();
    }

    @BeforeClass
    public static void startSphereClient() {
        sphereClient = provideSphereClient();
        final TaxCategoryProvider taxCategoryProvider = new TaxCategoryProvider(sphereClient);
        final ProductTypeProvider productTypeProvider = new ProductTypeProvider(sphereClient);
        product = new ProductProvider(sphereClient, taxCategoryProvider, productTypeProvider).get();
        cartType = new CartTypeProvider(sphereClient).get();
        System.out.println("Started sphereclient %%%%%");
    }

    @AfterClass
    public static void stopSphereClient() {
        System.out.println("Stopped sphereclient %%%%%%");
        if (sphereClient != null) {
            sphereClient.close();
            sphereClient = null;
        }
        product = null;
        cartType = null;
    }
}