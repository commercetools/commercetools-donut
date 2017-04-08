package donut.inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductDraftBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static io.sphere.sdk.client.SphereClientUtils.blockingWait;
import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;
import static java.util.concurrent.CompletableFuture.completedFuture;

@Singleton
public class ProductProvider implements Provider<ProductProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductProvider.class);
    private static final ProductDraft PRODUCT_DRAFT_WITHOUT_TAX_CATEGORY = readObjectFromResource("data/product-draft.json", ProductDraft.class);
    private static final TaxCategoryDraft TAX_CATEGORY_DRAFT = readObjectFromResource("data/tax-category-draft.json", TaxCategoryDraft.class);
    private static final ProductTypeDraft PRODUCT_TYPE_DRAFT = readObjectFromResource("data/product-type-draft.json", ProductTypeDraft.class);

    private final SphereClient sphereClient;

    @Inject
    public ProductProvider(final SphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public ProductProjection get() {
        LOGGER.debug("Providing product...");
        final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent();
        final CompletionStage<ProductProjection> productStage = sphereClient.execute(query)
                .thenCompose(result -> result
                        .head()
                        .map(product -> (CompletionStage) completedFuture(product))
                        .orElseGet(this::createProduct));
        return blockingWait(productStage, Duration.ofMinutes(1));
    }

    private CompletionStage<ProductProjection> createProduct() {
        LOGGER.debug("Product not found, creating product...");
        final CompletionStage<ProductType> productTypeStage = provideProductType();
        return provideTaxCategory()
                .thenCompose(taxCategory -> productTypeStage
                        .thenCompose(productType -> {
                            final ProductDraft productDraft = ProductDraftBuilder.of(PRODUCT_DRAFT_WITHOUT_TAX_CATEGORY)
                                    .taxCategory(taxCategory)
                                    .publish(true)
                                    .build();
                            final ProductCreateCommand command = ProductCreateCommand.of(productDraft);
                            return sphereClient.execute(command)
                                    .thenApply(product -> product.toProjection(ProductProjectionType.CURRENT));
                        }));
    }

    private CompletionStage<TaxCategory> provideTaxCategory() {
        LOGGER.debug("Providing tax category...");
        final TaxCategoryQuery query = TaxCategoryQuery.of().byName(TAX_CATEGORY_DRAFT.getName());
        return sphereClient.execute(query)
                .thenCompose(result -> result
                        .head()
                        .map(taxCategory -> (CompletionStage) completedFuture(taxCategory))
                        .orElseGet(this::createTaxCategory));
    }

    private CompletionStage<TaxCategory> createTaxCategory() {
        LOGGER.debug("Tax category not found, creating tax category...");
        final TaxCategoryCreateCommand command = TaxCategoryCreateCommand.of(TAX_CATEGORY_DRAFT);
        return sphereClient.execute(command);
    }

    private CompletionStage<ProductType> provideProductType() {
        LOGGER.debug("Providing product type...");
        final ProductTypeQuery query = ProductTypeQuery.of().byKey(PRODUCT_TYPE_DRAFT.getKey());
        return sphereClient.execute(query)
                .thenCompose(result -> result
                        .head()
                        .map(productType -> (CompletionStage) completedFuture(productType))
                        .orElseGet(this::createProductType));
    }

    private CompletionStage<ProductType> createProductType() {
        LOGGER.debug("Product type not found, creating product type...");
        final ProductTypeCreateCommand command = ProductTypeCreateCommand.of(PRODUCT_TYPE_DRAFT);
        return sphereClient.execute(command);
    }
}
