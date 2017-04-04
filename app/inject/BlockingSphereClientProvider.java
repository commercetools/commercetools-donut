package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

class BlockingSphereClientProvider implements Provider<BlockingSphereClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingSphereClientProvider.class);

    private final Configuration configuration;

    @Inject
    public BlockingSphereClientProvider(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public BlockingSphereClient get() {
        final String projectKey = requireNonNull(configuration.getString("sphere.project"));
        final String clientId =  requireNonNull(configuration.getString("sphere.clientId"));
        final String clientSecret = requireNonNull(configuration.getString("sphere.clientSecret"));
        final SphereClientFactory factory = SphereClientFactory.of();
        final SphereClient sphereClient = factory.createClient(projectKey, clientId, clientSecret);
        LOGGER.debug("Created BlockingSphereClient");
        return BlockingSphereClient.of(sphereClient, 30, TimeUnit.SECONDS);
    }
}
