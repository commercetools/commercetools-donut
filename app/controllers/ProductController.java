package controllers;

import exceptions.ProductNotFoundException;
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
    public ProductController(final Application application, ProductService productService, final CartService cartService) {
        super(application);
        this.productService = requireNonNull(productService);
        this.cartService = requireNonNull(cartService);
    }

    public Result show() {
        LOG.debug("Display Product page");
        final Cart currentCart = cartService.getOrCreateCart(session());
        LOG.debug("Current Cart[cartId={}]", currentCart.getId());
        final Optional<ProductVariant> selectedVariant = cartService.getSelectedVariant(currentCart);
        LOG.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
        final int selectedFrequency = cartService.getFrequency(currentCart.getId());
        LOG.debug("Selected frequency: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(productService.getProduct().get(), selectedVariant, selectedFrequency);
        return ok(index.render(productPageData));
    }

    public F.Promise<Result> _show() {
        LOG.debug("Display Product page");
        return cartService._getOrCreateCart(session()).flatMap(currentCart -> {

            final Optional<ProductVariant> selectedVariant = cartService.getSelectedVariant(currentCart);
            final F.Promise<Optional<ProductProjection>> productPromise = productService._getProduct();
            final F.Promise<Integer> selectedFrequencyPromise = cartService._getFrequency(currentCart.getId());

            return productPromise.flatMap(productProjection -> selectedFrequencyPromise.map(selectedFrequency -> {
                final ProductPageData productPageData = new ProductPageData(productProjection.orElseThrow(ProductNotFoundException::new),
                        selectedVariant, selectedFrequency);
                return ok(index.render(productPageData));
            }));
        });
    }

    public Result submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            final Optional<ProductVariant> selectedVariant = productService.getVariantFromId(productService.getProduct().get(), boundForm.get().getVariantId());
            LOG.debug("Selected ProductVariant[variantId={}]", selectedVariant.isPresent() ? selectedVariant.get().getId() : selectedVariant);
            if (selectedVariant.isPresent()) {
                final Cart currentCart = cartService.getOrCreateCart(session());
                LOG.debug("Current Cart[cartId={}]", currentCart.getId());
                cartService.setProductToCart(currentCart, productService.getProduct().get(), selectedVariant.get(), boundForm.get().getHowOften());
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
