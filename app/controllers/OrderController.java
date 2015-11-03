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
    private final String pactasPublicKey;

    @Inject
    public OrderController(final Application application, final CartService cartService,
                           final ProductProjection productProjection) {
        super(application, productProjection);
        this.cartService = requireNonNull(cartService);
        this.pactasPublicKey = requireNonNull(application.configuration().getString("pactas.publicKey"));
    }

    public F.Promise<Result> show() {
        F.Promise<Result> resultPromise;
        final Optional<Integer> optionalSelectedVariantId = CartSessionUtils.getSelectedVariantIdFromSession(session());
        final Integer selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());

        if (!optionalSelectedVariantId.isPresent()) {
            flash("error", "Please select a box and how often you want it.");
            resultPromise = F.Promise.pure(redirect(routes.ProductController.show()));
        } else if (selectedFrequency < 1) {
            flash("error", "Missing frequency of delivery. Please try selecting it again.");
            resultPromise = F.Promise.pure(redirect(routes.ProductController.show()));
        } else {
            final ProductVariant selectedVariant = productProjection().getVariant(optionalSelectedVariantId.get());
            final OrderPageData orderPageData = new OrderPageData(selectedVariant, selectedFrequency);
            resultPromise = F.Promise.pure(ok(order.render(orderPageData, pactasPublicKey)));
        }
        return resultPromise;
    }

    public F.Promise<Result> submit() {
        LOG.debug("Submitting Order details page");
        final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
        final F.Promise<Cart> deletedCartPromise = currentCartPromise.flatMap(cartService::deleteCart);
        return deletedCartPromise.map(cart -> {
            LOG.debug("Deleted Cart[{}]", cart.getId());
            CartSessionUtils.resetSession(session());
            return ok(success.render());
        });
    }

    public F.Promise<Result> clear() {
        LOG.debug("Clearing");
        final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
        return currentCartPromise.flatMap(currentCart -> {
            final F.Promise<Cart> deletedCartPromise = cartService.clearCart(currentCart);
            return deletedCartPromise.map(cart -> {
                LOG.debug("Deleted Cart[{}]", cart.getId());
                CartSessionUtils.resetSession(session());
                return redirect(routes.ProductController.show());
            });
        });
    }
}
