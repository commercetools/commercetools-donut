package services;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class ProductServiceImpl extends AbstractShopService implements ProductService {

    private final ProductProjection cachedProduct;

    public ProductServiceImpl(final SphereClient sphereClient) {
        super(sphereClient);
        final Optional<ProductProjection> product = Optional.of(loadProduct().orElseThrow(ProductNotFoundException::new));
        this.cachedProduct = product.get();
    }

    @Override
    public Optional<ProductVariant> getVariantFromId(ProductProjection product, int variantId) {
        return product.getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
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
