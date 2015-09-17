package services;

import com.google.inject.Singleton;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import java.util.Optional;

@Singleton
public class ProductServiceImpl extends AbstractShopService implements ProductService {

    private static final Logger.ALogger LOG = Logger.of(ProductServiceImpl.class);

    private final F.Promise<Optional<ProductProjection>> cachedProduct;

    @Inject
    public ProductServiceImpl(final ApplicationLifecycle applicationLifecycle,
                              final PlayJavaSphereClient playJavaSphereClient) {
        super(applicationLifecycle, playJavaSphereClient);
        this.cachedProduct = loadProduct();
    }

    private F.Promise<Optional<ProductProjection>> loadProduct() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final F.Promise<PagedQueryResult<ProductProjection>> productProjectionPagedQueryResultPromise =
                playJavaSphereClient().execute(request);
        return productProjectionPagedQueryResultPromise.map(productProjectionPagedQueryResult -> productProjectionPagedQueryResult.head());
    }

    @Override
    public F.Promise<Optional<ProductProjection>> getProduct() {
        return cachedProduct;
    }
}
