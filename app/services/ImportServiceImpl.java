package services;

import com.google.inject.Singleton;
import io.sphere.sdk.client.PlayJavaSphereClient;
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
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.libs.F;

import javax.inject.Inject;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class ImportServiceImpl extends AbstractShopService implements ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";
    private static final String TYPE_DRAFT_JSON_RESOURCE = "data/type-draft.json";

    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";

    @Inject
    public ImportServiceImpl(final PlayJavaSphereClient playJavaSphereClient, final Configuration configuration) {
        super(playJavaSphereClient);
        requireNonNull(configuration);
        final Boolean importEnabled = configuration.getBoolean("fixtures.import.enabled", false);
        logger.debug("Import enabled: {}", importEnabled);
        if(importEnabled) {
            importProductData();
            importCustomType();
        }
    }

    private void importProductData() {
        logger.debug("Starting Product import");
        currentProductData().onRedeem(existingProduct -> {
            logger.debug("Existing Product found: {}", existingProduct.isPresent());
            if(!existingProduct.isPresent()) {
                exportProductModel().onRedeem(product -> logger.debug("Finished Product import, created '{}'", product));
            }
        });
    }

    private F.Promise<Optional<ProductProjection>> currentProductData() {
        final ProductProjectionQuery query = ProductProjectionQuery.ofCurrent();
        final F.Promise<PagedQueryResult<ProductProjection>> productProjectionResultPromise =
                playJavaSphereClient().execute(query);

        return productProjectionResultPromise.map(PagedQueryResult::head);
    }


    private void importCustomType() {
        logger.debug("Starting custom Type import");
        currentCustomType().onRedeem(currentCustomType -> {
            logger.debug("Existing custom Type found: {}", currentCustomType.isPresent());
            if (!currentCustomType.isPresent()) {
                exportCustomType().onRedeem(type -> logger.debug("Finished custom Type import, created '{}'", type.getKey()));
            }
        });
    }

    private F.Promise<Optional<Type>> currentCustomType() {
        final TypeQuery query = TypeQuery.of();
        final F.Promise<PagedQueryResult<Type>> execute = playJavaSphereClient().execute(query);
        return execute.map(typePagedQueryResult ->
                        typePagedQueryResult.getResults().stream()
                                .filter(type -> CUSTOM_TYPE_KEY.equals(type.getKey()))
                                .findFirst()
        );
    }

    @Override
    public F.Promise<Type> exportCustomType() {
        final TypeDraft typeDraft = SphereJsonUtils.readObjectFromResource(TYPE_DRAFT_JSON_RESOURCE, TypeDraft.class);
        return playJavaSphereClient().execute(TypeCreateCommand.of(typeDraft));
    }

    @Override
    public F.Promise<Product> exportProductModel() {
        final F.Promise<TaxCategory> taxCategoryPromise = createTaxCategoryModel();
        final F.Promise<ProductType> productTypePromise = createProductTypeModel();

        return taxCategoryPromise.flatMap(taxCategory -> productTypePromise.flatMap(productType -> {
            final ProductDraft productDraft = SphereJsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE, ProductDraft.class);
            return playJavaSphereClient().execute(ProductCreateCommand.of(productDraft))
                    .flatMap(product -> playJavaSphereClient().execute(ProductUpdateCommand.of(product, Publish.of())));
        }));
    }

    private F.Promise<ProductType> createProductTypeModel() {
        final ProductTypeDraft productTypeDraft = SphereJsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraft.class);
        return playJavaSphereClient().execute(ProductTypeCreateCommand.of(productTypeDraft));
    }

    private F.Promise<TaxCategory> createTaxCategoryModel() {
        final TaxCategoryDraft taxCategoryDraft = SphereJsonUtils.readObjectFromResource(TAX_CATEGORY_JSON_RESOURCE, TaxCategoryDraft.class);
        return playJavaSphereClient().execute(TaxCategoryCreateCommand.of(taxCategoryDraft));
    }
}
