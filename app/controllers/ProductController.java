package controllers;

import exceptions.ProductNotFoundException;
import exceptions.ProductVariantNotFoundException;
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
import services.ProductService;
import views.html.index;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static play.data.Form.form;

public class ProductController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(ProductController.class);

    private final static Form<SubscriptionFormData> ADD_TO_CART_FORM = form(SubscriptionFormData.class);
    private final ProductService productService;
    private final CartService cartService;

    @Inject
    public ProductController(final Application application, ProductService productService, final CartService cartService,
                             final ProductProjection productProjection) {
        super(application, productProjection);
        this.productService = requireNonNull(productService);
        this.cartService = requireNonNull(cartService);
    }

    public F.Promise<Result> show() {
        LOG.debug("Display Product page");
        return cartService.getOrCreateCart(session()).flatMap(
                currentCart -> {
                    final Optional<ProductVariant> selectedVariant = cartService.getSelectedVariant(currentCart);
                    final F.Promise<Optional<ProductProjection>> productPromise = productService.getProduct();
                    final F.Promise<Integer> selectedFrequencyPromise = cartService.getFrequency(currentCart.getId());
                    return productPromise.flatMap(productProjection -> selectedFrequencyPromise.map(selectedFrequency -> {
                        final ProductPageData productPageData = new ProductPageData(productProjection.orElseThrow(ProductNotFoundException::new),
                                selectedVariant, selectedFrequency);
                        return ok(index.render(productPageData));
                    }));
                });
    }

    public F.Promise<Result> submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final SubscriptionFormData subscriptionFormData = boundForm.get();
            final int frequency = subscriptionFormData.getHowOften();
            final int variantId = subscriptionFormData.getVariantId();
            return setVariantToCart(frequency, variantId)
                    .map(cart -> redirect(routes.OrderController.show()));
        } else {
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        }
    }

    private F.Promise<Cart> setVariantToCart(int frequency, int variantId) {
        final F.Promise<Optional<ProductProjection>> productPromise = productService.getProduct();
        final F.Promise<Cart> currentCartPromise = cartService.getOrCreateCart(session());
        return productPromise.flatMap(productProjectionOptional -> {
            final ProductProjection productProjection = productProjectionOptional.orElseThrow(ProductNotFoundException::new);
            final ProductVariant selectedVariant = Optional.ofNullable(productProjection.getVariant(variantId)).orElseThrow(ProductVariantNotFoundException::new);
            return currentCartPromise.flatMap(currentCart -> cartService.setProductToCart(currentCart, productProjection, selectedVariant, frequency));
        });
    }
}
