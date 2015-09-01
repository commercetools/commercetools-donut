package models;

import io.sphere.sdk.models.Base;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewProductPageData extends Base {

    private final Optional<ProductVariant> selectedVariant;
    private final ProductProjection product;
    private final int selectedFrequency;

    public NewProductPageData(final ProductProjection product, final Optional<ProductVariant> selectedVariant,
                              final int selectedFrequency) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.product = product;
    }

    public Optional<NewVariantData> selectedVariant() {
        if (selectedVariant.isPresent()) {
            return Optional.of(new NewVariantData(selectedVariant.get()));
        } else {
            return Optional.empty();
        }
    }

    public int selectedFrequency() {
        return selectedFrequency;
    }

    public List<NewVariantData> allVariants() {
        final List<NewVariantData> variantDataList = product.getAllVariants().stream().map(NewVariantData::new)
                .collect(Collectors.toList());
        return variantDataList;
    }
}
