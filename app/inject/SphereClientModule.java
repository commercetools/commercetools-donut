package inject;

import com.google.inject.AbstractModule;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.PlayJavaSphereClient;

import javax.inject.Singleton;

public class SphereClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BlockingSphereClient.class).toProvider(SphereClientProvider.class).in(Singleton.class);
        bind(PlayJavaSphereClient.class).toProvider(PlayJavaSphereClientProvider.class).in(Singleton.class);
    }
}
