package services;

import io.sphere.sdk.client.SphereClient;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public abstract class AbstractShopService {

    private final SphereClient sphereClient;

    @Inject
    protected AbstractShopService(final SphereClient sphereClient) {
        this.sphereClient = requireNonNull(sphereClient);
    }

    protected SphereClient sphereClient() {
        return sphereClient;
    }
}
