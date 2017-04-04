package controllers;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import models.OrderPageData;
import play.Configuration;
import play.Logger;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import services.CartService;
import services.CartSessionUtils;
import views.html.order;
import views.html.success;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

public class OrderController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(OrderController.class);

    private final CartService cartService;
    private final String pactasPublicKey;

    @Inject
    public OrderController(final Configuration configuration, final CartService cartService,
                           final ProductProjection productProjection) {
        super(configuration, productProjection);
        this.cartService = requireNonNull(cartService);
        this.pactasPublicKey = requireNonNull(configuration.getString("pactas.publicKey"));
    }

    public Result show() {
        final Optional<Integer> optionalSelectedVariantId = CartSessionUtils.getSelectedVariantIdFromSession(session());
        final Integer selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());
        if (!optionalSelectedVariantId.isPresent()) {
            flash("error", "Please select a box and how often you want it.");
            return redirect(routes.ProductController.show());
        } else if (selectedFrequency < 1) {
            flash("error", "Missing frequency of delivery. Please try selecting it again.");
            return redirect(routes.ProductController.show());
        } else {
            final ProductVariant selectedVariant = productProjection().getVariant(optionalSelectedVariantId.get());
            final OrderPageData orderPageData = new OrderPageData(selectedVariant, selectedFrequency);
            return ok(order.render(orderPageData, pactasPublicKey));
        }
    }

    public CompletionStage<Result> submit() {
        LOG.debug("Submitting Order details page");
        return cartService.getOrCreateCart(session())
                .thenCompose(cartService::deleteCart)
                .thenApplyAsync(cart -> {
                    LOG.debug("Deleted Cart[{}]", cart.getId());
                    CartSessionUtils.resetSession(session());
                    return ok(success.render());
                }, HttpExecution.defaultContext());
    }

    public CompletionStage<Result> clear() {
        LOG.debug("Clearing");
        return cartService.getOrCreateCart(session())
                .thenCompose(currentCart -> cartService.clearCart(currentCart)
                        .thenApplyAsync(clearedCart -> {
                            LOG.debug("Cleared Cart[{}]", clearedCart.getId());
                            CartSessionUtils.resetSession(session());
                            return redirect(routes.ProductController.show());
                        }, HttpExecution.defaultContext()));
    }
}
