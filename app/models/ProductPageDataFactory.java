package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import donut.services.SubscriptionInSession;

import javax.inject.Inject;

public class ProductPageDataFactory extends Base {

    private final ProductProjection product;
    private final SubscriptionInSession subscriptionInSession;

    @Inject
    public ProductPageDataFactory(final ProductProjection product, final SubscriptionInSession subscriptionInSession) {
        this.product = product;
        this.subscriptionInSession = subscriptionInSession;
    }

    public ProductPageData create() {
        final ProductVariant variant = subscriptionInSession.findVariant().orElse(null);
        final Integer frequency = subscriptionInSession.findFrequency().orElse(null);
        return new ProductPageData(product.getAllVariants(), variant, frequency);
    }
}
