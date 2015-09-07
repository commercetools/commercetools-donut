package services;

import io.sphere.sdk.client.SphereClient;

import static java.util.Objects.requireNonNull;

public abstract class AbstractShopService {

    private final SphereClient sphereClient;

    protected AbstractShopService(final SphereClient sphereClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
    }

    protected SphereClient sphereClient() {
        return sphereClient;
    }
}
