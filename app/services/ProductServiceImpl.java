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

public class ProductServiceImpl implements ProductService {

    private final SphereClient sphereClient;
    private final ProductProjection cachedProduct;

    public ProductServiceImpl(final SphereClient sphereClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
        final Optional<ProductProjection> product = loadProduct();
        if(!product.isPresent()) {
            throw new ProductNotFoundException();
        }
        this.cachedProduct = product.get();
    }

    @Override
    public Optional<ProductVariant> getVariantFromId(ProductProjection product, int variantId) {
        return product.getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    private Optional<ProductProjection> loadProduct() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final CompletionStage<PagedQueryResult<ProductProjection>> resultCompletionStage =
                sphereClient.execute(request);
        final PagedQueryResult<ProductProjection> queryResult = resultCompletionStage.toCompletableFuture().join();
        return queryResult.head();
    }

    @Override
    public Optional<ProductProjection> getProduct() {
       return Optional.of(cachedProduct);

    }
}
