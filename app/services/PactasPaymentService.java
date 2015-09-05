package services;

import io.sphere.sdk.client.SphereClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sphere.Sphere;

import static java.util.Objects.requireNonNull;

public class PactasPaymentService implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PactasPaymentService.class);
    private final SphereClient sphereClient;
    private final Sphere deprecatedClient;

    public PactasPaymentService(SphereClient sphereClient, Sphere deprecatedClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
        this.deprecatedClient = requireNonNull(deprecatedClient, "'deprecatedClient' must not be null");
    }
}
