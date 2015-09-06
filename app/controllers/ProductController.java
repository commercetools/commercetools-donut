package controllers;

import forms.SubscriptionFormData;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.products.ProductVariant;
import models.ProductPageData;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import services.ShopService;
import views.html.index;

import java.util.Optional;

import static play.data.Form.form;

public class ProductController extends BaseController {
    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);

    public ProductController(final Configuration configuration, final ShopService cartService) {
        super(configuration, cartService);
    }

    public Result show() {
        Logger.debug("Display Product page");
        final Cart currentCart = shopService().getOrCreateCart(session());
        Logger.debug("Current Cart[cartId={}]", currentCart.getId());
        final Optional<ProductVariant> selectedVariant = shopService().getSelectedVariant(currentCart);
        Logger.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
        final int selectedFrequency = shopService().getFrequency(currentCart.getId());
        Logger.debug("Selected frequency: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(product(), selectedVariant, selectedFrequency);
        return ok(index.render(productPageData));
    }

    public Result submit() {
        Logger.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final Optional<ProductVariant> selectedVariant = shopService().variantFromId(product(), boundForm.get().variantId);
            Logger.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
            if (selectedVariant.isPresent()) {
                final Cart currentCart = shopService().getOrCreateCart(session());
                Logger.debug("Current Cart[cartId={}]", currentCart.getId());
                shopService().setProductToCart(currentCart, product(), selectedVariant.get(), boundForm.get().howOften);
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
