package controllers;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.mvc.Result;
import services.CartService;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public class OrderController extends BaseController {

    private final CartService cartService;

    @Inject
    public OrderController(final Configuration configuration, final CartService cartService) {
        super(configuration);
        this.cartService = requireNonNull(cartService, "'cartService' must not be null");
    }

    public Result show() {
        final Cart currentCart = cartService.getOrCreateCart(session());
        if (!currentCart.getLineItems().isEmpty()) {
            final int selectedFrequency = cartService.getFrequency(currentCart.getId());
            if (selectedFrequency > 0) {
                final ProductVariant selectedVariant = currentCart.getLineItems().get(0).getVariant();
                final OrderPageData orderPageData = new OrderPageData(selectedVariant, selectedFrequency, currentCart);
                //return ok(order.render(orderPageData));
                return ok();
            } else {
                flash("error", "Missing frequency of delivery. Please try selecting it again.");
            }
        }
        return redirect(routes.ProductController.show());
    }

    public Result submit() {
        try {
            final Cart currentCart = cartService.getOrCreateCart(session());
            final Cart clearedCart = cartService.clearCart(currentCart);
            //return ok(success.render());
            return ok();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }

    public Result clear() {
        try {
            final Cart currentCart = cartService.getOrCreateCart(session());
            final Cart clearedCart = cartService.clearCart(currentCart);
            return redirect(routes.ProductController.show());
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            return internalServerError();
        }
    }
}
