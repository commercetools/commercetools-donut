package controllers;

import forms.SubscriptionFormData;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import models.ProductPageData;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import services.CartService;
import services.CartSessionUtils;
import views.html.index;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class ProductController extends BaseController {

    private static final Logger.ALogger LOG = Logger.of(ProductController.class);

    private final CartService cartService;
    private final FormFactory formFactory;

    @Inject
    public ProductController(final Configuration configuration, final CartService cartService,
                             final ProductProjection productProjection, final FormFactory formFactory) {
        super(configuration, productProjection);
        this.cartService = requireNonNull(cartService);
        this.formFactory = formFactory;
    }

    public CompletionStage<Result> show() {
        LOG.debug("Display Product page");
        Optional<ProductVariant> selectedVariant = Optional.empty();

        final Optional<Integer> selectedVariantId = CartSessionUtils.getSelectedVariantIdFromSession(session());
        if (selectedVariantId.isPresent()) {
            selectedVariant = Optional.of(productProjection().getVariant(selectedVariantId.get()));
        }
        final int selectedFrequency = CartSessionUtils.getSelectedFrequencyFromSession(session());
        final ProductPageData productPageData = new ProductPageData(productProjection(), selectedVariant,
                selectedFrequency);
        return completedFuture(ok(index.render(productPageData)));
    }

    public CompletionStage<Result> submit() {
        LOG.debug("Submitting Product page");
        final Form<SubscriptionFormData> boundForm = formFactory.form(SubscriptionFormData.class).bindFromRequest();
        if (!boundForm.hasErrors()) {
            CartSessionUtils.clearProductFromSession(session());
            final SubscriptionFormData subscriptionFormData = boundForm.get();
            final int frequency = subscriptionFormData.getHowOften();
            final int variantId = subscriptionFormData.getVariantId();
            LOG.debug("Received form data: frequency[{}], variantId[{}]", frequency, variantId);
            final ProductVariant selectedVariant = productProjection().getVariant(variantId);
            return cartService.getOrCreateCart(session())
                    .thenCompose(cartService::clearCart)
                    .thenCompose(clearedCart -> cartService.setProductToCart(clearedCart, selectedVariant.getIdentifier(), frequency))
                    .thenApplyAsync(updatedCart -> {
                        CartSessionUtils.writeCartSessionData(session(), updatedCart.getId(), variantId, frequency);
                        return redirect(routes.OrderController.show());
                    }, HttpExecution.defaultContext());
        } else {
            flash("error", "Please select a box and how often you want it.");
            return completedFuture(redirect(routes.ProductController.show()));
        }
    }
}
