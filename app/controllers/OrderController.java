package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import sphere.Sphere;
import views.html.order;
import views.html.success;

import java.util.Optional;

public class OrderController extends BaseController {

    public OrderController(final Sphere sphere, final Configuration configuration, final ProductProjection productProjection,
                           final SphereClient sphereClient) {
        super(sphere, configuration, productProjection, sphereClient);
    }

    public Result show() {
        final Cart cart = sphere().currentCart().fetch();
        if (cart.getLineItems().size() > 0) {
            final int selectedFrequency = frequency(cart.getId());
            if (selectedFrequency > 0) {
                final Variant selectedVariant = cart.getLineItems().get(0).getVariant();
                final Optional<ProductVariant> selectedProductVariant = mapToProductVariant(java.util.Optional.of(selectedVariant));
                final OrderPageData orderPageData = new OrderPageData(selectedProductVariant.get(), selectedFrequency, cart);
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
