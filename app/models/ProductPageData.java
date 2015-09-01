package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductPageData extends Base {

    private final Optional<ProductVariant> selectedVariant;
    private final ProductProjection product;
    private final int selectedFrequency;

    public ProductPageData(final ProductProjection product, final Optional<ProductVariant> selectedVariant,
                           final int selectedFrequency) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.product = product;
    }

    public Optional<VariantData> selectedVariant() {
        if (selectedVariant.isPresent()) {
            return Optional.of(new VariantData(selectedVariant.get()));
        } else {
            return Optional.empty();
        }
    }

    public int selectedFrequency() {
        return selectedFrequency;
    }

    public List<VariantData> allVariants() {
        final List<VariantData> variantDataList = product.getAllVariants().stream().map(VariantData::new)
                .collect(Collectors.toList());
        return variantDataList;
    }
}
