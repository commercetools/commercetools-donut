package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductVariant;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ProductPageData extends Base {

    private final List<ProductVariant> variants;
    @Nullable
    private final ProductVariant variant;
    @Nullable
    private final Integer frequency;

    ProductPageData(final List<ProductVariant> variants, @Nullable final ProductVariant variant, @Nullable final Integer frequency) {
        this.variants = variants;

        this.variant = variant;
        this.frequency = frequency;
    }

    public Optional<SubscriptionViewModel> selectedVariant() {
        return Optional.ofNullable(variant)
                .map(SubscriptionViewModel::new);
    }

    @Nullable
    public Integer selectedFrequency() {
        return frequency;
    }

    public List<SubscriptionViewModel> allVariants() {
        return variants.stream()
                .map(SubscriptionViewModel::new)
                .collect(toList());
    }
}
