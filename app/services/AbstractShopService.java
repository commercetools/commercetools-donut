package services;

import io.sphere.sdk.client.PlayJavaSphereClient;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public abstract class AbstractShopService {

    private final PlayJavaSphereClient playJavaSphereClient;

    @Inject
    protected AbstractShopService(final PlayJavaSphereClient playJavaSphereClient) {
        this.playJavaSphereClient = requireNonNull(playJavaSphereClient);
    }

    protected PlayJavaSphereClient playJavaSphereClient() {
        return playJavaSphereClient;
    }
}
