package controllers;

import forms.SubscriptionFormData;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import models.ProductPageData;
import play.Application;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.mvc.Result;
import services.CartService;
import services.CartSessionUtils;
import views.html.index;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static play.data.Form.form;

public class ProductController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(ProductController.class);

    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);
    private final CartService cartService;

    @Inject
    public ProductController(final Application application, final CartService cartService,
                             final ProductProjection productProjection) {
        super(application, productProjection);
        this.cartService = requireNonNull(cartService);
    }

    public F.Promise<Result> show() {
        LOG.debug("Display Product page");
        Optional<ProductVariant> selectedVariant = Optional.empty();

        final Optional<Integer> selectedVariantId = CartSessionUtils.getSelectedVariantIdFromSession(session());
        if (selectedVariantId.isPresent()) {
            selectedVariant = Optional.of(productProjection().getVariant(selectedVariantId.get()));
        }
        LOG.debug("Selected variantId from session: {}", selectedVariantId);
        final int selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());
        LOG.debug("Selected frequency from session: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(productProjection(), selectedVariant, selectedFrequency);
        return F.Promise.promise(() -> ok(index.render(productPageData)));
    }

    public F.Promise<Result> submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            CartSessionUtils.clearProductFromSession(session());
            final SubscriptionFormData subscriptionFormData = boundForm.get();
            final int frequency = subscriptionFormData.getHowOften();
            final int variantId = subscriptionFormData.getVariantId();
            final ProductVariant selectedVariant = productProjection().getVariant(variantId);

            final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
            final F.Promise<Cart> clearedCartPromise = currentCartPromise.flatMap(cartService::clearCart);
            final F.Promise<Cart> updatedCartPromise = clearedCartPromise.flatMap(clearedCart ->
                    cartService.setProductToCart(clearedCart, productProjection(), selectedVariant, frequency));

            return updatedCartPromise.map(updatedCart -> {
                CartSessionUtils.writeCartSessionData(updatedCart, session());
                return redirect(routes.OrderController.show());});
        } else {
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        }
    }
}
