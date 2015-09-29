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
        return cartService.getOrCreateCart(session()).flatMap(
                currentCart -> {
                    final Optional<ProductVariant> selectedVariant = cartService.getSelectedVariant(currentCart);
                    final F.Promise<Integer> selectedFrequencyPromise = cartService.getFrequency(currentCart.getId());
                    return selectedFrequencyPromise.map(selectedFrequency -> {
                        final ProductPageData productPageData = new ProductPageData(productProjection(), selectedVariant, selectedFrequency);
                        return ok(index.render(productPageData));

                    });
                });
    }

    public F.Promise<Result> submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final SubscriptionFormData subscriptionFormData = boundForm.get();
            final int frequency = subscriptionFormData.getHowOften();
            final int variantId = subscriptionFormData.getVariantId();
            final ProductVariant selectedVariant = productProjection().getVariant(variantId);
            final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());

            return currentCartPromise.map(currentCart -> {
                cartService.setProductToCart(currentCart, productProjection(), selectedVariant, frequency);
                return redirect(routes.OrderController.show());
            });
        } else {
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        }
    }
}
