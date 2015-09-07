package services;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

public class ProductServiceImpl implements ProductService {

    private final SphereClient sphereClient;

    public ProductServiceImpl(final SphereClient sphereClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
    }

    @Override
    public Optional<ProductVariant> getVariantFromId(ProductProjection product, int variantId) {
        return product.getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    @Override
    public Optional<ProductProjection> getProduct() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final CompletionStage<PagedQueryResult<ProductProjection>> resultCompletionStage =
                sphereClient.execute(request);
        final PagedQueryResult<ProductProjection> queryResult = resultCompletionStage.toCompletableFuture().join();
        return queryResult.head();
    }
}
