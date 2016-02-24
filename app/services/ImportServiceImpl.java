package services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Singleton;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.json.SphereJsonUtils;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeByKeyGet;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class ImportServiceImpl implements ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";
    private static final String TYPE_DRAFT_JSON_RESOURCE = "data/type-draft.json";

    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";
    private final BlockingSphereClient sphereClient;

    @Inject
    public ImportServiceImpl(final BlockingSphereClient sphereClient, final Configuration configuration) {
        this.sphereClient = sphereClient;
        requireNonNull(configuration);
        final Boolean importEnabled = configuration.getBoolean("fixtures.import.enabled", false);
        logger.debug("Import enabled: {}", importEnabled);
        if(importEnabled) {
            importProductData();
            importCustomType();
        }
    }

    private void importProductData() {
        final Optional<ProductProjection> productProjectionOptional =
                sphereClient.executeBlocking(ProductProjectionQuery.ofCurrent()).head();
        if (!productProjectionOptional.isPresent()) {
            logger.debug("Starting Product import");
            final Optional<TaxCategory> taxCategoryOptional = sphereClient.executeBlocking(TaxCategoryQuery.of()).head();
            final TaxCategory taxCategory = taxCategoryOptional.orElseGet(() -> {
                final TaxCategoryDraft taxCategoryDraft =
                        SphereJsonUtils.readObjectFromResource(TAX_CATEGORY_JSON_RESOURCE, TaxCategoryDraft.class);
                return sphereClient.executeBlocking(TaxCategoryCreateCommand.of(taxCategoryDraft));
            });
            final ProductType productType = Optional.ofNullable(sphereClient.executeBlocking(ProductTypeByKeyGet.of("donut-box")))
                    .orElseGet(() -> {
                        final ProductTypeDraft productTypeDraft = SphereJsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraft.class);
                        return sphereClient.executeBlocking(ProductTypeCreateCommand.of(productTypeDraft));
                    });
            final ObjectNode productDraftJson = (ObjectNode) JsonUtils.readJsonFromResource(PRODUCT_JSON_RESOURCE);
            ((ObjectNode) productDraftJson.get("taxCategory")).put("id", taxCategory.getId());
            final ProductDraft productDraft = SphereJsonUtils.readObject(productDraftJson, ProductDraft.class);
            final Product product = sphereClient.executeBlocking(ProductCreateCommand.of(productDraft));
            final Product publishedProduct = sphereClient.executeBlocking(ProductUpdateCommand.of(product, Publish.of()));
            logger.debug("Finished Product import, created '{}'", publishedProduct);
        }
    }

    private Type importCustomType() {
        return sphereClient.executeBlocking(TypeQuery.of().withPredicates(m -> m.key().is(CUSTOM_TYPE_KEY))).head()
                .orElseGet(() -> {
                    final TypeDraft typeDraft = SphereJsonUtils.readObjectFromResource(TYPE_DRAFT_JSON_RESOURCE, TypeDraft.class);
                    return sphereClient.executeBlocking(TypeCreateCommand.of(typeDraft));
                });
    }
}
