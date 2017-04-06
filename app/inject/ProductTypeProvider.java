package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;

import javax.inject.Inject;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class ProductTypeProvider implements Provider<ProductType> {

    private final BlockingSphereClient sphereClient;

    @Inject
    public ProductTypeProvider(final BlockingSphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public ProductType get() {
        return sphereClient.executeBlocking(ProductTypeQuery.of().byKey("donut-box"))
                .head()
                .orElseGet(this::createProductType);
    }

    private ProductType createProductType() {
        final ProductTypeDraft productTypeDraft = readObjectFromResource("data/product-type-draft.json", ProductTypeDraft.class);
        return sphereClient.executeBlocking(ProductTypeCreateCommand.of(productTypeDraft));
    }
}
