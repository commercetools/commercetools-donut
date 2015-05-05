package models;

import io.sphere.client.shop.model.Cart;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;

import java.util.ArrayList;
import java.util.List;

public class ProductPageData extends PageData {
    private final Product product;

    public ProductPageData(final Cart cart, final int frequency, final Product product) {
        super(cart, frequency);
        this.product = product;
    }

    public List<VariantData> allVariants() {
        final List<VariantData> variantDataList = new ArrayList<>();
        for (final Variant variant : product.getVariants().asList()) {
            variantDataList.add(new VariantData(variant));
        }
        return variantDataList;
    }

}
