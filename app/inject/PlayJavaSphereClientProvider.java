package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.client.SphereClient;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayJavaSphereClientProvider implements Provider<PlayJavaSphereClient> {

    private static final Logger.ALogger LOG = Logger.of(PlayJavaSphereClientProvider.class);

    private final ApplicationLifecycle applicationLifecycle;
    private final SphereClient sphereClient;

    @Inject
    public PlayJavaSphereClientProvider(final ApplicationLifecycle applicationLifecycle, final BlockingSphereClient sphereClient) {
        this.applicationLifecycle = applicationLifecycle;
        this.sphereClient = sphereClient;
    }

    @Override
    public PlayJavaSphereClient get() {
        final PlayJavaSphereClient playJavaSphereClient = PlayJavaSphereClient.of(sphereClient);
        applicationLifecycle.addStopHook(() ->
                        F.Promise.promise(() -> {
                            playJavaSphereClient.close();
                            return null;
                        })
        );
        LOG.debug("Created PlaySphereClient");
        return playJavaSphereClient;
    }
}
