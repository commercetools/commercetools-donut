package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class BlockingSphereClientProvider implements Provider<BlockingSphereClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingSphereClientProvider.class);

    private final Configuration configuration;
    private final ApplicationLifecycle applicationLifecycle;

    @Inject
    public BlockingSphereClientProvider(final Configuration configuration, final ApplicationLifecycle applicationLifecycle) {
        this.configuration = configuration;
        this.applicationLifecycle = applicationLifecycle;
    }

    @Override
    public BlockingSphereClient get() {
        final String projectKey = requireNonNull(configuration.getString("sphere.project"));
        final String clientId =  requireNonNull(configuration.getString("sphere.clientId"));
        final String clientSecret = requireNonNull(configuration.getString("sphere.clientSecret"));
        final SphereClient sphereClient = SphereClientFactory.of().createClient(projectKey, clientId, clientSecret);
        final BlockingSphereClient blockingSphereClient = BlockingSphereClient.of(sphereClient, 30, TimeUnit.SECONDS);
        LOGGER.debug("Created BlockingSphereClient");
        applicationLifecycle.addStopHook(() -> {
            LOGGER.debug("Closing BlockingSphereClient");
            blockingSphereClient.close();
            return completedFuture(null);
        });
        return blockingSphereClient;
    }
}
