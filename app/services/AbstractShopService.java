package services;

import io.sphere.sdk.client.PlayJavaSphereClient;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public abstract class AbstractShopService {

    private static final Logger.ALogger LOG = Logger.of(AbstractShopService.class);

    private final ApplicationLifecycle applicationLifecycle;
    private final PlayJavaSphereClient playJavaSphereClient;

    @Inject
    protected AbstractShopService(final ApplicationLifecycle applicationLifecycle,
                                  final PlayJavaSphereClient playJavaSphereClient) {
        this.applicationLifecycle = requireNonNull(applicationLifecycle);
        this.playJavaSphereClient = playJavaSphereClient;
    }

    protected PlayJavaSphereClient playJavaSphereClient() {
        return playJavaSphereClient;
    }
}
