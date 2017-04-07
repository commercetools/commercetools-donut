package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;

import static io.sphere.sdk.client.SphereClientUtils.blockingWait;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

public class ProductProvider implements Provider<ProductProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProvider.class);
    private static final ProductDraft PRODUCT_DRAFT = readObjectFromResource("data/product-draft.json", ProductDraft.class);

    private final SphereClient sphereClient;
    private final TaxCategoryProvider taxCategoryProvider;
    private final ProductTypeProvider productTypeProvider;

    @Inject
    public ProductProvider(final SphereClient sphereClient, final TaxCategoryProvider taxCategoryProvider, final ProductTypeProvider productTypeProvider) {
        this.sphereClient = sphereClient;
        this.taxCategoryProvider = taxCategoryProvider;
        this.productTypeProvider = productTypeProvider;
    }

    @Override
    public ProductProjection get() {
        LOGGER.debug("Providing product...");
        final TaxCategory taxCategory = taxCategoryProvider.get();
        final ProductType productType = productTypeProvider.get();
        final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent().byProductType(productType);
        return blockingWait(sphereClient.execute(query), Duration.ofSeconds(30))
                .head()
                .orElseGet(() -> createProduct(taxCategory));
    }

    private ProductProjection createProduct(final TaxCategory taxCategory) {
        LOGGER.debug("Product not found, creating product...");
        final ProductDraft productDraftWithTaxCategory = ProductDraftBuilder.of(PRODUCT_DRAFT)
                .taxCategory(taxCategory)
                .publish(true)
                .build();
        final ProductCreateCommand command = ProductCreateCommand.of(productDraftWithTaxCategory);
        return blockingWait(sphereClient.execute(command), Duration.ofSeconds(30))
                .toProjection(ProductProjectionType.CURRENT);
    }
}
