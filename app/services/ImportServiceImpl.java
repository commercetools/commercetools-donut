package services;

import com.google.inject.Singleton;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import models.wrapper.ProductDraftWrapper;
import models.wrapper.ProductTypeDraftWrapper;
import models.wrapper.TaxCategoryWrapper;
import play.Configuration;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class ImportServiceImpl extends AbstractShopService implements ImportService {

    private static final Logger.ALogger LOG = Logger.of(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    @Inject
    public ImportServiceImpl(final PlayJavaSphereClient playJavaSphereClient, final Configuration configuration) {
        super(playJavaSphereClient);
        requireNonNull(configuration);
        final Boolean importEnabled = configuration.getBoolean("fixtures.import.enabled", false);
        LOG.debug("Import enabled: {}", importEnabled);
        if (importEnabled) {
            importProductData();
        }
    }

    private void importProductData() {
        LOG.debug("Starting Product import");
        currentProductData().onRedeem(existingProduct -> {
            LOG.debug("Existing Product found: {}", existingProduct.isPresent());
            if (!existingProduct.isPresent()) {
                exportProductModel().onRedeem(product -> LOG.debug("Finished Product import, created '{}'", product));
            }
        });
    }

    private F.Promise<Optional<ProductProjection>> currentProductData() {
        final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent();
        final F.Promise<PagedQueryResult<ProductProjection>> productProjectionResultPromise =
                playJavaSphereClient().execute(query);

        return productProjectionResultPromise.map(PagedQueryResult::head);
    }

    @Override
    public F.Promise<Product> exportProductModel() {
        final F.Promise<TaxCategory> taxCategoryPromise = createTaxCategoryModel();
        final F.Promise<ProductType> productTypePromise = createProductTypeModel();

        return taxCategoryPromise.flatMap(taxCategory -> productTypePromise.flatMap(productType -> {
            final ProductDraftWrapper productDraftWrapper = JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE,
                    ProductDraftWrapper.class);

            final ProductDraft productDraft = productDraftWrapper.createProductDraft(productType.toReference(),
                    taxCategory.toReference());
            return playJavaSphereClient().execute(ProductCreateCommand.of(productDraft))
                    .flatMap(product -> playJavaSphereClient().execute(ProductUpdateCommand.of(product, Publish.of())));
        }));
    }

    private F.Promise<ProductType> createProductTypeModel() {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        return playJavaSphereClient()
                .execute(ProductTypeCreateCommand.of(productTypeDraftWrapper.createProductTypeDraft()));
    }

    private F.Promise<TaxCategory> createTaxCategoryModel() {
        final TaxCategoryWrapper taxCategoryWrapper = JsonUtils.readObjectFromResource(TAX_CATEGORY_JSON_RESOURCE,
                TaxCategoryWrapper.class);
        return playJavaSphereClient().execute(TaxCategoryCreateCommand.of(taxCategoryWrapper.createTaxCategoryDraft()));
    }
}
