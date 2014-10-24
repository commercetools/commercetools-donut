package utils;

import io.sphere.client.exceptions.SphereException;
import io.sphere.client.shop.model.*;
import sphere.Session;
import sphere.Sphere;

public class Util {


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
