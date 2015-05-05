package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.shop.model.Cart;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import sphere.Sphere;
import views.html.order;
import views.html.success;

public class OrderController extends BaseController {

    public OrderController(final Sphere sphere, final Configuration configuration) {
        super(sphere, configuration);
    }

    public Result show() {
        final Cart cart = sphere().currentCart().fetch();
        if (cart.getLineItems().size() > 0) {
            final int frequency = frequency(cart.getId());
            if (frequency > 0) {
                final OrderPageData orderPageData = new OrderPageData(cart, frequency);
                return ok(order.render(orderPageData));
            } else {
                flash("error", "Missing frequency of delivery. Please try selecting it again.");
            }
        }
        return redirect(routes.ProductController.show());
    }

    public Result submit() {
        try {
            final Cart cart = sphere().currentCart().fetch();
            clearLineItemsFromCurrentCart(cart.getLineItems());
            clearFrequency(cart.getId());
            return ok(success.render());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    public Result clear() {
        try {
            final Cart cart = sphere().currentCart().fetch();
            clearLineItemsFromCurrentCart(cart.getLineItems());
            clearFrequency(cart.getId());
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
