package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static io.sphere.sdk.client.SphereClientUtils.blockingWait;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class ProductTypeProvider implements Provider<ProductType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductTypeProvider.class);
    private static final ProductTypeDraft PRODUCT_TYPE_DRAFT = readObjectFromResource("data/product-type-draft.json", ProductTypeDraft.class);

    private final SphereClient sphereClient;

    @Inject
    public ProductTypeProvider(final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public ProductType get() {
        LOGGER.debug("Providing product type...");
        final ProductTypeQuery query = ProductTypeQuery.of().byKey(PRODUCT_TYPE_DRAFT.getKey());
        return blockingWait(sphereClient.execute(query), Duration.ofSeconds(30))
                .head()
                .orElseGet(this::createProductType);
    }

    private ProductType createProductType() {
        LOGGER.debug("Product type not found, creating product type...");
        final ProductTypeCreateCommand command = ProductTypeCreateCommand.of(PRODUCT_TYPE_DRAFT);
        return blockingWait(sphereClient.execute(command), Duration.ofSeconds(30));
    }
}
