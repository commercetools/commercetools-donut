package donut.services;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import play.mvc.Http.Session;

import javax.inject.Inject;
import java.util.Optional;

public class SubscriptionInSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionInSession.class);
    private static final String FREQUENCY = "frequency";
    private static final String VARIANT_ID = "variant-id";

    private final ProductProjection product;

    @Inject
    public SubscriptionInSession(final ProductProjection product) {
        this.product = product;
    }

    public Optional<Integer> findFrequency() {
        return findIntegerValueFromSession(FREQUENCY);
    }

    public Optional<ProductVariant> findVariant() {
        return findIntegerValueFromSession(VARIANT_ID)
                .flatMap(variantId -> product.getAllVariants().stream()
                        .filter(variant -> variant.getId().equals(variantId))
                        .findAny());
    }

    public void store(final int variantId, final int frequency) {
        session().put(VARIANT_ID, String.valueOf(variantId));
        session().put(FREQUENCY, String.valueOf(frequency));
        LOGGER.debug("Stored subscription in session: {}", session().toString());
    }

    public void remove() {
        session().remove(FREQUENCY);
        session().remove(VARIANT_ID);
        LOGGER.debug("Removed subscription from session");
    }

    private static Session session() {
        return Http.Context.current().session();
    }

    private Optional<Integer> findIntegerValueFromSession(final String sessionKey) {
        return Optional.ofNullable(session().get(sessionKey))
                .flatMap(valueAsString -> {
                    try {
                        return Optional.of(Integer.valueOf(valueAsString));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                });
    }
}