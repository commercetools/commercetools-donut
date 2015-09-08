package services;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import java.util.Optional;

/**
 * Business service that provides access to the SPHERE.IO Product API.
 */
public interface ProductService {

    /**
     * Return the optional {@code ProductVariant} with the given variantId
     *
     * @param product the {@code ProductProjection}, must not be null
     * @param variantId id of the {@code ProductVariant}
     * @return
     */
    Optional<ProductVariant> getVariantFromId(final ProductProjection product, final int variantId);


    /**
     * Return the optional {@code ProductProjection}.
     * Because this shop is just a simple demonstration, it consists of only
     * one {@code ProductProjection} with different {@code ProductVariant}s.
     * So this is the only Product query, result will be service-internally stored on application startup.
     * For demonstrational purpose only!
     *
     * @return optional {@code ProductProjection}, the result of the Product API call, maybe empty
     */
    Optional<ProductProjection> getProduct();
}
