package inject;

import com.google.inject.Provider;
import exceptions.ProductNotFoundException;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Singleton
public class ProductProvider implements Provider<ProductProjection> {

    private final PlayJavaSphereClient playJavaSphereClient;
    private static final long ALLOWED_TIMEOUT = 3000;


    @Inject
    public ProductProvider(final PlayJavaSphereClient playJavaSphereClient) {
        this.playJavaSphereClient = requireNonNull(playJavaSphereClient);
    }

    @Override
    public  ProductProjection get() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final F.Promise<PagedQueryResult<ProductProjection>> productProjectionPagedQueryResultPromise =
                playJavaSphereClient.execute(request);
        //blocking on application startup, fail fast
        return productProjectionPagedQueryResultPromise.get(ALLOWED_TIMEOUT, TimeUnit.MILLISECONDS).head()
                .orElseThrow(() -> new ProductNotFoundException());
    }
}
