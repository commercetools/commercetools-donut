package donut.inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

@Singleton
public class SphereClientProvider implements Provider<SphereClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SphereClientProvider.class);

    private final Configuration configuration;
    private final ApplicationLifecycle applicationLifecycle;

    @Inject
    public SphereClientProvider(final Configuration configuration, final ApplicationLifecycle applicationLifecycle) {
        this.configuration = configuration;
        this.applicationLifecycle = applicationLifecycle;
    }

    @Override
    public SphereClient get() {
        final String projectKey = requireNonNull(configuration.getString("sphere.project"));
        final String clientId =  requireNonNull(configuration.getString("sphere.clientId"));
        final String clientSecret = requireNonNull(configuration.getString("sphere.clientSecret"));
        final SphereClient sphereClient = SphereClientFactory.of().createClient(projectKey, clientId, clientSecret);
        LOGGER.debug("Created SphereClient");

        applicationLifecycle.addStopHook(() -> {
            LOGGER.debug("Closing SphereClient");
            sphereClient.close();
            return completedFuture(null);
        });
        return sphereClient;
    }
}
