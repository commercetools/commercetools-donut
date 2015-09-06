package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import services.ShopService;
import views.html.order;
import views.html.success;

import java.util.Optional;

public class OrderController extends BaseController {

    public OrderController(final Configuration configuration, final ShopService cartService) {
        super(configuration, cartService);
    }

    public Result show() {
        final Cart currentCart = shopService().getOrCreateCart(session());
        if (currentCart.getLineItems().size() > 0) {
            final int selectedFrequency = shopService().getFrequency(currentCart.getId());
            if (selectedFrequency > 0) {
                final Optional<ProductVariant> selectedVariant = Optional.of(currentCart.getLineItems().get(0).getVariant());
                final OrderPageData orderPageData = new OrderPageData(selectedVariant.get(), selectedFrequency, currentCart);
                return ok(order.render(orderPageData));
            } else {
                flash("error", "Missing frequency of delivery. Please try selecting it again.");
            }
        }
        return redirect(routes.ProductController.show());
    }

    public Result submit() {
        try {
            final Cart currentCart = shopService().getOrCreateCart(session());
            final Cart clearedCart = shopService().clearCart(currentCart);
            return ok(success.render());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    public Result clear() {
        try {
            final Cart currentCart = shopService().getOrCreateCart(session());
            final Cart clearedCart = shopService().clearCart(currentCart);
            return redirect(routes.ProductController.show());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }
}
