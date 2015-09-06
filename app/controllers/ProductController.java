package controllers;

import forms.SubscriptionFormData;
import io.sphere.client.SphereClientException;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.ProductPageData;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import services.CartService;
import views.html.index;

import java.util.Optional;

import static play.data.Form.form;

public class ProductController extends BaseController {
    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);

    public ProductController(final Configuration configuration, final CartService cartService) {
        super(configuration, cartService);
    }

    public Result show() {
        Logger.debug("Display Product page");
        final Cart currentCart = cartService().createOrGet(session());
        Logger.debug("Current Cart: {}", currentCart);
        final Optional<ProductVariant> selectedVariant = getSelectedVariant(currentCart);
        Logger.debug("Selected variant: {}", selectedVariant);
        final int selectedFrequency = cartService().getFrequency(currentCart.getId());
        Logger.debug("Selected frequency: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(product(), selectedVariant, selectedFrequency);
        return ok(index.render(productPageData));
    }

    public Result submit() {
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final Optional<ProductVariant> variant = variant(boundForm.get().variantId);
            if (variant.isPresent()) {
                try {
                    final Cart currentCart = cartService().createOrGet(session());
                    cartService().setProductToCart(currentCart, product(), variant.get(), boundForm.get().howOften);
                    return redirect(routes.OrderController.show());
                } catch (SphereClientException e) {
                    Logger.error(e.getMessage(), e);
                }
            } else {
                flash("error", "Product not found. Please try again.");
            }
        } else {
            flash("error", "Please select a box and how often you want it.");
        }
        return redirect(routes.ProductController.show());
    }

    private Optional<ProductVariant> getSelectedVariant(final Cart cart) {
        final Optional<ProductVariant> selectedVariant =
                (cart.getLineItems().size() > 0)
                        ? Optional.ofNullable(cart.getLineItems().get(0).getVariant())
                        : Optional.empty();
        return selectedVariant;
    }
}
