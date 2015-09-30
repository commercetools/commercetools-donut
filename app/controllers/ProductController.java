package controllers;

import forms.SubscriptionFormData;
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
import services.SessionKeys;
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
        LOG.debug("Selected variant from session: {}", selectedVariant);
        final int selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());
        LOG.debug("Selected frequency from session: {}", selectedFrequency);
        final ProductPageData productPageData = new ProductPageData(productProjection(), selectedVariant, selectedFrequency);
        return F.Promise.promise(() -> ok(index.render(productPageData)));
    }

    public F.Promise<Result> submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = ADD_TO_CART_FORM.bindFromRequest();
        if (!boundForm.hasErrors()) {
            //TODO
            session().clear();
            //CartSessionUtils.clearSession(session());
            //session().put(SessionKeys.VARIANT_ID, null);
            //session().put(SessionKeys.FREQUENCY, null);
            final SubscriptionFormData subscriptionFormData = boundForm.get();
            final int frequency = subscriptionFormData.getHowOften();
            final int variantId = subscriptionFormData.getVariantId();
            final ProductVariant selectedVariant = productProjection().getVariant(variantId);

            session().put(SessionKeys.VARIANT_ID, String.valueOf(selectedVariant.getId()));
            LOG.debug("Add variantId[{}] to session", selectedVariant.getId());
            session().put(SessionKeys.FREQUENCY, String.valueOf(selectedVariant.getId()));
            LOG.debug("Add frequency[{}] to session", frequency);


            return cartService.getOrCreateCart(session())
                    .flatMap(cart -> cartService.setProductToCart(cart, productProjection(), selectedVariant, frequency))
                    .map(updatedCart -> redirect(routes.OrderController.show()));

        } else {
            flash("error", "Please select a box and how often you want it.");
            return F.Promise.pure(redirect(routes.ProductController.show()));
        }
    }
}
