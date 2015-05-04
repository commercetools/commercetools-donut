package controllers;

import com.google.common.base.Optional;
import forms.SubscriptionFormData;
import io.sphere.client.SphereClientException;
import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Variant;
import models.ProductPageData;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import sphere.Sphere;
import views.html.index;

import static play.data.Form.form;

public class ProductController extends BaseController {
    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);

    public ProductController(final Sphere sphere, final Configuration configuration) {
        super(sphere, configuration);
    }

    public Result show() {
        final Cart cart = sphere().currentCart().fetch();
        final int frequency = fetchFrequency(cart.getId());
        final ProductPageData productPageData = new ProductPageData(cart, frequency, product());
        return ok(index.render(productPageData));
    }

    public Result submit() {
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final Optional<Variant> variant = variant(boundForm.get().variantId);
            if (variant.isPresent()) {
                try {
                    setProductToCart(variant.get(), boundForm.get().howOften);
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

    private void setProductToCart(final Variant variant, final int frequency) {
        final Cart cart = sphere().currentCart().fetch();
        clearLineItemsFromCurrentCart(cart.getLineItems());
        sphere().currentCart().addLineItem(product().getId(), variant.getId(), 1);
        sphere().customObjects().set(FREQUENCY, cart.getId(), frequency).get();
    }
}
