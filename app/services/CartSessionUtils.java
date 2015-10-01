package services;

import play.Logger;
import play.mvc.Http;
import play.mvc.Http.Session;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class CartSessionUtils {

    private static final Logger.ALogger LOG = Logger.of(CartSessionUtils.class);

    public static Optional<Integer> getSelectedVariantIdFromSession(final Http.Session session) {
        try {
            return Optional.of(Integer.parseInt(session.get(SessionKeys.VARIANT_ID)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Integer getSelectedFrequencyFromSession(final Http.Session session) {
        try {
            return Integer.parseInt(session.get(SessionKeys.FREQUENCY));
        } catch (Exception e) {
            return 0;
        }
    }

    public static void writeCartSessionData(final Session session, final String cartId, final int variantId, final int frequency) {
        requireNonNull(session);
        session.put(SessionKeys.CART_ID, cartId);
        session.put(SessionKeys.VARIANT_ID, String.valueOf(variantId));
        session.put(SessionKeys.FREQUENCY, String.valueOf(frequency));
        LOG.debug("Wrote session data: {}", session.toString());
    }


    public static void clearProductFromSession(final Session session) {
        session.remove(SessionKeys.FREQUENCY);
        session.remove(SessionKeys.VARIANT_ID);
        LOG.debug("Cleared product data from session");
    }
}