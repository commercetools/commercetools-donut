package services;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

public class ProductServiceImpl extends AbstractShopService implements ProductService {

    private final ProductProjection cachedProduct;

    public ProductServiceImpl(final SphereClient sphereClient) {
        super(sphereClient);
        this.cachedProduct = loadProduct().orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Optional<ProductVariant> getVariantFromId(ProductProjection product, int variantId) {
        requireNonNull(product, "'product' must not be null");
        return Optional.ofNullable(product.getVariant(variantId));
    }

    private Optional<ProductProjection> loadProduct() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final CompletionStage<PagedQueryResult<ProductProjection>> resultCompletionStage =
                sphereClient().execute(request);
        final PagedQueryResult<ProductProjection> queryResult = resultCompletionStage.toCompletableFuture().join();
        return queryResult.head();
    }

    @Override
    public Optional<ProductProjection> getProduct() {
       return Optional.of(cachedProduct);

    }
}
