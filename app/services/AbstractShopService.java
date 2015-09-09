package services;

import io.sphere.sdk.client.SphereClient;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public abstract class AbstractShopService {

    private static final Logger.ALogger LOG = Logger.of(AbstractShopService.class);

    private final SphereClient sphereClient;
    private final ApplicationLifecycle applicationLifecycle;

    @Inject
    protected AbstractShopService(final SphereClient sphereClient, final ApplicationLifecycle applicationLifecycle) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
        this.applicationLifecycle = requireNonNull(applicationLifecycle, "'applicationLifecycle' must not be null");
        this.applicationLifecycle.addStopHook(() -> {
            if (this.sphereClient != null) {
                this.sphereClient.close();
                LOG.debug("Shutting down SphereClient");
            }
            return F.Promise.pure(null);
        });
    }

    protected SphereClient sphereClient() {
        return sphereClient;
    }
}
