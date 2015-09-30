package services;

import play.mvc.Http;
import play.mvc.Http.Session;

import java.util.Optional;

public final class CartSessionUtils {

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

    public static void clearSession(final Session session) {
        session.remove(SessionKeys.FREQUENCY);
        session.remove(SessionKeys.VARIANT_ID);
    }
}