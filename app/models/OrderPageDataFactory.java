package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductVariant;
import pactas.PactasConfiguration;

import javax.inject.Inject;

public class OrderPageDataFactory extends Base {

    private final PactasConfiguration pactasConfiguration;

    @Inject
    public OrderPageDataFactory(final PactasConfiguration pactasConfiguration) {
        this.pactasConfiguration = pactasConfiguration;
    }

    public OrderPageData create(final ProductVariant variant, final int frequency) {
        return new OrderPageData(variant, frequency, pactasConfiguration.getPublicKey());
    }
}
