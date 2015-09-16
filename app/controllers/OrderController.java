package controllers;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Application;
import play.Logger;
import play.libs.F;
import play.mvc.Result;
import services.CartService;
import views.html.order;
import views.html.success;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

public class OrderController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(OrderController.class);

    private final CartService cartService;

    @Inject
    public OrderController(final Application application, final CartService cartService) {
        super(application);
        this.cartService = requireNonNull(cartService);
    }

    public F.Promise<Result> show() {
        LOG.debug("Display Order details page");
        final F.Promise<Cart> currentCartPromise = cartService._getOrCreateCart(session());
        return currentCartPromise.flatMap(currentCart -> {
            if (!currentCart.getLineItems().isEmpty()) {
                //TODO
                //sometimes this seems to be 0 and the flash message occurs, sometimes nt
                final F.Promise<Integer> selectedFrequencyPromise = cartService._getFrequency(currentCart.getId());
                final F.Promise<Result> resultPromise = selectedFrequencyPromise.map(selectedFrequency -> {
                    if (selectedFrequency > 0) {
                        final ProductVariant selectedVariant = currentCart.getLineItems().get(0).getVariant();
                        final OrderPageData orderPageData = new OrderPageData(selectedVariant, selectedFrequency, currentCart);
                        return ok(order.render(orderPageData));
                    } else {
                        flash("error", "Missing frequency of delivery. Please try selecting it again.");
                        return redirect(routes.ProductController.show());
                    }
                });
                return resultPromise;
            }
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        });
    }

    //TODO check is doing nothing than clear the cart?!
    public F.Promise<Result> submit() {
        LOG.debug("Submitting Order details page");
        final F.Promise<Cart> currentCartPromise = cartService._getOrCreateCart(session());
        return currentCartPromise.map(currentCart -> ok(success.render()));
    }

    public F.Promise<Result> clear() {
        final F.Promise<Cart> currentCartPromise = cartService._getOrCreateCart(session());
        return currentCartPromise.flatMap(currentCart -> {
            final F.Promise<Cart> clearedCartPromise = cartService._clearCart(currentCart);
            return clearedCartPromise.map(cart -> redirect(routes.ProductController.show()));
        });
    }
}
