package models;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductPageData {
    private final Optional<Variant> selectedVariant;
    private final int selectedFrequency;
    private final Product product;

    public ProductPageData(final Optional<Variant> selectedVariant, final int selectedFrequency, final Product product) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.product = product;
    }

    public Optional<VariantData> selectedVariant() {
        if (selectedVariant.isPresent()) {
            return Optional.of(new VariantData(selectedVariant.get()));
        } else {
            return Optional.absent();
        }
    }

    public int selectedFrequency() {
        return selectedFrequency;
    }

    public List<VariantData> allVariants() {
        final List<VariantData> variantDataList = product.getVariants().asList().stream().map(VariantData::new)
                .collect(Collectors.toList());
        return variantDataList;
    }

}
