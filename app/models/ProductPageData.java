package models;

import com.google.common.base.Optional;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProductPageData {
    private final Optional<Variant> selectedVariant;
    private final int selectedFrequency;
    private final Product product;


    private final Optional<ProductVariant> selectedProductVariant;
    private final ProductProjection productProjection;



    public ProductPageData(final Optional<Variant> selectedVariant, final int selectedFrequency, final Product product,
                           final ProductProjection productProjection, final Optional<ProductVariant> selectedProductVariant) {
        this.selectedVariant = selectedVariant;
        this.selectedFrequency = selectedFrequency;
        this.product = product;

        this.productProjection = productProjection;
        this.selectedProductVariant = selectedProductVariant;
    }

    public Optional<VariantData> selectedVariant() {
        if (selectedVariant.isPresent()) {
            return Optional.of(new VariantData(selectedVariant.get(), selectedProductVariant.get()));
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
            variantDataList.add(new VariantData(variant, null));
        }
        return variantDataList;
    }

}
