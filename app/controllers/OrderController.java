package controllers;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Application;
import play.Logger;
import play.libs.F;
import play.mvc.Result;
import services.CartService;
import services.CartSessionUtils;
import views.html.order;
import views.html.success;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class OrderController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(OrderController.class);

    private final CartService cartService;

    @Inject
    public OrderController(final Application application, final CartService cartService,
                           final ProductProjection productProjection) {
        super(application, productProjection);
        this.cartService = requireNonNull(cartService);
    }

    public F.Promise<Result> show() {
        LOG.debug("Display Order details page");
        final Optional<Integer> optionalSelectedVariantId = CartSessionUtils.getSelectedVariantIdFromSession(session());
        if(optionalSelectedVariantId.isPresent()) {
            final Integer selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());
            LOG.debug("OrderController received variantId[{}], frequency[{}]: ",
                    CartSessionUtils.getSelectedVariantIdFromSession(session()), selectedFrequency);
            if (selectedFrequency > 0) {
                final ProductVariant selectedVariant = productProjection().getVariant(optionalSelectedVariantId.get());
                final OrderPageData orderPageData = new OrderPageData(selectedVariant, selectedFrequency);
                return F.Promise.pure(ok(order.render(orderPageData)));
            } else {
                flash("error", "Missing frequency of delivery. Please try selecting it again.");
                return F.Promise.pure(redirect(routes.ProductController.show()));
            }
        } else {
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        }
    }

    //TODO check is doing nothing than clear the cart?!
    public F.Promise<Result> submit() {
        LOG.debug("Submitting Order details page");
        final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
        return currentCartPromise.map(currentCart -> ok(success.render()));
    }

    public F.Promise<Result> clear() {
        final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
        return currentCartPromise.flatMap(currentCart -> {
            final F.Promise<Cart> clearedCartPromise = cartService.clearCart(currentCart);
            return clearedCartPromise.map(cart -> redirect(routes.ProductController.show()));
        });
    }
}
