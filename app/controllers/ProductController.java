package controllers;

import forms.SubscriptionFormData;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.ProductPageData;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import services.CartService;
import services.ProductService;
import views.html.index;

import java.util.Optional;

import static play.data.Form.form;

public class ProductController extends BaseController {

    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);

    public ProductController(final Configuration configuration, ProductService productService, final CartService cartService) {
        super(configuration, productService, cartService);
    }

    public Result show() {
        Logger.debug("Display Product page");
        final Cart currentCart = cartService().getOrCreateCart(session());
        Logger.debug("Current Cart[cartId={}]", currentCart.getId());
        final Optional<ProductVariant> selectedVariant = cartService().getSelectedVariant(currentCart);
        Logger.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
        final int selectedFrequency = cartService().getFrequency(currentCart.getId());
        Logger.debug("Selected frequency: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(product(), selectedVariant, selectedFrequency);
        return ok(index.render(productPageData));
    }

    public Result submit() {
        Logger.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final Optional<ProductVariant> selectedVariant = productService().getVariantFromId(product(), boundForm.get().variantId);
            Logger.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
            if (selectedVariant.isPresent()) {
                final Cart currentCart = cartService().getOrCreateCart(session());
                Logger.debug("Current Cart[cartId={}]", currentCart.getId());
                cartService().setProductToCart(currentCart, product(), selectedVariant.get(), boundForm.get().howOften);
                return redirect(routes.OrderController.show());
            } else {
                flash("error", "Product not found. Please try again.");
            }
        } else {
            flash("error", "Please select a box and how often you want it.");
        }
        return redirect(routes.ProductController.show());
    }
}
