package utils;

import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.LineItem;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import sphere.Session;
import sphere.Sphere;

public class Util {

    public static Product getProduct() {
        return Sphere.getInstance().products().bySlug("donut-box").fetch().orNull();
    }

    public static Variant getVariant(String pactasId) {
        for (Variant variant : getProduct().getVariants().asList()) {
            if (variant.getString("pactas1").equals(pactasId) ||
                variant.getString("pactas2").equals(pactasId) ||
                variant.getString("pactas4").equals(pactasId)) {
                return variant;
            }
        }
        return null;
    }

    public static void clearCart() {
        // Clear session cart
        Session.current().clearCart();
        // Remove all line items
        Cart cart = Sphere.getInstance().currentCart().fetch();
        for (LineItem item : cart.getLineItems()) {
            cart = Sphere.getInstance().currentCart().removeLineItem(item.getId());
        }
        // Remove frequency value
        try {
            if (Sphere.getInstance().customObjects().get("cart-frequency", cart.getId()).fetch().isPresent()) {
                Sphere.getInstance().customObjects().delete("cart-frequency", cart.getId()).execute();
            }
        } catch (Exception e) {}
    }

    public static boolean isValidCartSnapshot(String cartSnapshot) {
        try {
            Sphere.getInstance().currentCart().isSafeToCreateOrder(cartSnapshot);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
