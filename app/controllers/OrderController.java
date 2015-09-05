package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.client.exceptions.SphereException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import services.CartService;
import services.PaymentService;
import views.html.order;
import views.html.success;

import java.util.Optional;

public class OrderController extends BaseController {

    public OrderController(final CartService cartService, final PaymentService paymentService, final Configuration configuration) {
        super(cartService, paymentService, configuration);
    }

    public Result show() {
        final Cart cart = currentCart();
        if (cart.getLineItems().size() > 0) {
            final int selectedFrequency = frequency(cart.getId());
            if (selectedFrequency > 0) {
                final Optional<ProductVariant> selectedVariant = Optional.of(cart.getLineItems().get(0).getVariant());
                final OrderPageData orderPageData = new OrderPageData(selectedVariant.get(), selectedFrequency, cart);
                return ok(order.render(orderPageData));
            } else {
                flash("error", "Missing frequency of delivery. Please try selecting it again.");
            }
        }
        return redirect(routes.ProductController.show());
    }

    public Result submit() {
        try {
            final Cart clearedCart = clearLineItemsFromCurrentCart(currentCart());
            clearFrequency(clearedCart.getId());
            return ok(success.render());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    public Result clear() {
        try {
            final Cart clearedCart = clearLineItemsFromCurrentCart(currentCart());
            clearFrequency(clearedCart.getId());
            return redirect(routes.ProductController.show());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    private void clearFrequency(final String cartId) {
        try {
            sphere().customObjects().delete(FREQUENCY, cartId).execute();
        } catch (SphereException e) {
            // Assume already removed
            Logger.info(e.getMessage(), e);
        }
    }
}
