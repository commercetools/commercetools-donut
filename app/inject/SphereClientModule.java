package inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;

import javax.inject.Singleton;

public class SphereClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BlockingSphereClient.class).toProvider(BlockingSphereClientProvider.class).in(Singleton.class);
    }

    @Provides
    SphereClient provideSphereClient(final BlockingSphereClient blockingSphereClient) {
        return blockingSphereClient;
    }
}
