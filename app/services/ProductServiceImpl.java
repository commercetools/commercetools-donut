package services;

import com.google.inject.Singleton;
import exceptions.ProductNotFoundException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

@Singleton
public class ProductServiceImpl extends AbstractShopService implements ProductService {

    private static final Logger.ALogger LOG = Logger.of(ProductServiceImpl.class);

    private final ProductProjection cachedProduct;

    @Inject
    public ProductServiceImpl(final SphereClient sphereClient,  final ApplicationLifecycle applicationLifecycle) {
        super(sphereClient, applicationLifecycle);
        this.cachedProduct = loadProduct().orElseThrow(ProductNotFoundException::new);
        LOG.debug("Fetched Product from Sphere: {}", cachedProduct.getName());
    }

    @Override
    public Optional<ProductVariant> getVariantFromId(ProductProjection product, int variantId) {
        requireNonNull(product);
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
