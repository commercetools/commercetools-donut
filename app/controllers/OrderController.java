package controllers;

import io.sphere.client.SphereClientException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import services.CartService;
import services.ProductService;
import views.html.order;
import views.html.success;

import java.util.Optional;

public class OrderController extends BaseController {

    public OrderController(final Configuration configuration, final ProductService productService, final CartService cartService) {
        super(configuration, productService, cartService);
    }

    public Result show() {
        final Cart currentCart = cartService().getOrCreateCart(session());
        if (currentCart.getLineItems().size() > 0) {
            final int selectedFrequency = cartService().getFrequency(currentCart.getId());
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
            final Cart currentCart = cartService().getOrCreateCart(session());
            final Cart clearedCart = cartService().clearCart(currentCart);
            return ok(success.render());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    public Result clear() {
        try {
            final Cart currentCart = cartService().getOrCreateCart(session());
            final Cart clearedCart = cartService().clearCart(currentCart);
            return redirect(routes.ProductController.show());
        } catch (SphereClientException e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }
}
