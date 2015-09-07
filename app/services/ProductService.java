package services;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.Optional;

public interface ProductService {

    Optional<ProductVariant> getVariantFromId(final ProductProjection product, final int variantId);

    Optional<ProductProjection> getProduct();
}
