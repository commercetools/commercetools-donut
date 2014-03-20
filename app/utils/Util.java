package utils;

import com.google.common.base.Optional;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.*;
import sphere.Session;
import sphere.Sphere;

public class Util {

    public final static String FREQUENCY    = "cart-frequency";
    public final static String PRODUCT      = "donut-box";
    public final static String ID_MONTHLY   = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY    = "pactas1";

    public static Sphere sphere() {
        return Sphere.getInstance();
    }

    public static Product getProduct() {
        return sphere().products().bySlug(PRODUCT).fetch().orNull();
    }

    public static Variant getVariant(int variantId) {
        return getProduct().getVariants().byId(variantId).orNull();
    }

    public static Variant getVariant(String pactasId) {
        if (pactasId == null) return null;
        for (Variant variant : getProduct().getVariants().asList()) {
            if (pactasId.equals(variant.getString(ID_MONTHLY)) ||
                pactasId.equals(variant.getString(ID_TWO_WEEKS)) ||
                pactasId.equals(variant.getString(ID_WEEKLY))) {
                return variant;
            }
        }
        return null;
    }

    public static int getFrequency(String key) {
        try {
            if (sphere().customObjects().get(FREQUENCY, key).fetch().isPresent()) {
                return sphere().customObjects().get(FREQUENCY, key).fetch().get().getValue().asInt();
            }
        } catch (SphereException se) {
            return 0;
        }
        return 0;
    }

    public static void setFrequency(String key, int frequency) {
        sphere().customObjects().set(FREQUENCY, key, frequency);
    }

    public static void unsetFrequency(String key) {
        try {
            if (sphere().customObjects().get(FREQUENCY, key).fetch().orNull() != null) {
                sphere().customObjects().delete(FREQUENCY, key).execute();
            }
        } catch (SphereException se) {
            // Assume it is already unset
        }
    }

    public static void addLineItem(Variant variant, int frequency) {
        clearLineItems(sphere().currentCart().fetch());
        Cart cart = sphere().currentCart().addLineItem(getProduct().getId(), variant.getId(), 1);
        setFrequency(cart.getId(), frequency);
    }

    public static Cart clearLineItems(Cart cart) {
        CartUpdate update = new CartUpdate();
        for (LineItem item : cart.getLineItems()) {
            update.removeLineItem(item.getId());
        }
        return sphere().currentCart().update(update);
    }

    public static void clearCart() {
        Cart cart = sphere().currentCart().fetch();
        unsetFrequency(cart.getId());
        Session.current().clearCart();
    }
}
