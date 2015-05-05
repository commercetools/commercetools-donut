package models;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;

import java.util.ArrayList;
import java.util.List;

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
        final List<VariantData> variantDataList = new ArrayList<>();
        for (final Variant variant : product.getVariants().asList()) {
            variantDataList.add(new VariantData(variant));
        }
        return variantDataList;
    }

}
