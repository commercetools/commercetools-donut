package controllers;

import com.google.common.base.Optional;
import forms.SubscriptionFormData;
import io.sphere.client.SphereClientException;
import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.carts.queries.CartQueryModel;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
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

    public ProductController(final Sphere sphere, final Configuration configuration, final Product product, final SphereClient sphereClient, final ProductProjection productProjection) {
        super(sphere, configuration, product, sphereClient, productProjection);
    }

    public Result show() {
        final Cart cart = sphere().currentCart().fetch();
        final Optional<Variant> selectedVariant = getSelectedVariant(cart);

        final java.util.Optional<ProductVariant> selectedProductVariant = null; //TODO

        final int selectedFrequency = frequency(cart.getId());
        final ProductPageData productPageData = new ProductPageData(selectedVariant, selectedFrequency, product());
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

    private Optional<Variant> getSelectedVariant(final Cart cart) {
        final Optional<Variant> selectedVariant;
        if (cart.getLineItems().size() > 0) {
            selectedVariant = Optional.of(cart.getLineItems().get(0).getVariant());
        } else {
            selectedVariant = Optional.absent();
        }
        return selectedVariant;
    }

    private void setProductToCart(final Variant variant, final int frequency) {
        final Cart cart = sphere().currentCart().fetch();
        clearLineItemsFromCurrentCart(cart.getLineItems());
        final Cart updatedCart = sphere().currentCart().addLineItem(product().getId(), variant.getId(), 1);
        sphere().customObjects().set(FREQUENCY, updatedCart.getId(), frequency).get();
    }
}
