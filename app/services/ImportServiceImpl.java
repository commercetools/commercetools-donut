package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductProjectionType;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.products.commands.ProductUpdateCommand;
import io.sphere.sdk.products.commands.updateactions.Publish;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import models.wrapper.ProductDraftWrapper;
import models.wrapper.ProductTypeDraftWrapper;
import models.wrapper.TaxCategoryWrapper;
import play.Configuration;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.*;

import static java.util.Objects.requireNonNull;

@Singleton
public class ImportServiceImpl extends AbstractShopService implements ImportService {

    private static final Logger.ALogger LOG = Logger.of(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";
    private static final String CUSTOM_TYPE_NAME = "custom type for delivery frequency";
    private static final String FREQUENCY_FIELD_NAME = "frequency";
    private static final String FREQUENCY_FIELD_LABEL = "selected frequency";
    private static final long ALLOWED_TIMEOUT = 5000;

    @Inject
    public ImportServiceImpl(final PlayJavaSphereClient playJavaSphereClient, final Configuration configuration) {
        super(playJavaSphereClient);
        requireNonNull(configuration);
        final Boolean importEnabled = configuration.getBoolean("fixtures.import.enabled", false);
        LOG.debug("Import enabled: {}", importEnabled);
        if(importEnabled) {
            importData();
            importCustomType();
        }
    }

    private void importData() {
        LOG.debug("Starting Product import");
        productExists().flatMap(productExits -> {
            LOG.debug("Existing Product found: {}", productExits);
            return (productExits) ?
                    F.Promise.pure(null) :
                    exportProductModel().map(product -> {
                        LOG.debug("Finished Product import, created '{}'",
                                product.toProjection(ProductProjectionType.CURRENT).getName());
                        return null;
                    });
        }).get(ALLOWED_TIMEOUT);
    }

    private void importCustomType() {
        LOG.debug("Starting custom Type import");
        customTypeExists().flatMap(customTypeExists -> {
            LOG.debug("Existing custom Type found: {}", customTypeExists);
            return (customTypeExists) ?
                    F.Promise.pure(null) :
                    exportCustomType().map(type -> {
                        LOG.debug("Finished custom Type import, created '{}'", type.getKey());
                        return null;
                    });
        }).get(ALLOWED_TIMEOUT);
    }

    private F.Promise<Boolean> customTypeExists() {
        final TypeQuery query = TypeQuery.of();
        final F.Promise<PagedQueryResult<Type>> execute = playJavaSphereClient().execute(query);
        return execute.map(typePagedQueryResult -> {
            final Optional<Type> optionalType = typePagedQueryResult.getResults().stream()
                    .filter(type -> CUSTOM_TYPE_KEY.equals(type.getKey())).findFirst();
            return optionalType.isPresent();
        });
    }

    private F.Promise<Boolean> productExists() {
        final ProductProjectionQuery request = ProductProjectionQuery.ofCurrent();
        final F.Promise<PagedQueryResult<ProductProjection>> productProjectionResultPromise =
                playJavaSphereClient().execute(request);
        return productProjectionResultPromise.map(productProjectionPagedQueryResult ->
                !productProjectionPagedQueryResult.getResults().isEmpty());
    }

    @Override
    public F.Promise<Type> exportCustomType() {
        final TypeDraft customTypeDraft = frequencyTypeDefinition();
        final F.Promise<Type> customTypePromise = playJavaSphereClient().execute(TypeCreateCommand.of(customTypeDraft));
        return customTypePromise;
    }

    private static TypeDraft frequencyTypeDefinition() {
        final LocalizedString typeName = LocalizedString.of(Locale.ENGLISH, CUSTOM_TYPE_NAME);
        final String cartResourceTypeId = Cart.resourceTypeId();
        final Set<String> resourceTypeIds = Collections.singleton(cartResourceTypeId);
        final List<FieldDefinition> fieldDefinitions = Collections.singletonList(frequencyFieldDefinition());

        return TypeDraftBuilder.of(CUSTOM_TYPE_KEY, typeName, resourceTypeIds).fieldDefinitions(fieldDefinitions).build();
    }

    private static FieldDefinition frequencyFieldDefinition() {
        final LocalizedString frequencyFieldLabel = LocalizedString.of(Locale.ENGLISH, FREQUENCY_FIELD_LABEL);
        return FieldDefinition.of(StringType.of(), FREQUENCY_FIELD_NAME, frequencyFieldLabel, false, TextInputHint.SINGLE_LINE);
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
        return playJavaSphereClient().execute(ProductTypeCreateCommand.of(productTypeDraftWrapper.createProductTypeDraft()));
    }

    private F.Promise<TaxCategory> createTaxCategoryModel() {
        final TaxCategoryWrapper taxCategoryWrapper = JsonUtils.readObjectFromResource(TAX_CATEGORY_JSON_RESOURCE,
                TaxCategoryWrapper.class);
        return playJavaSphereClient().execute(TaxCategoryCreateCommand.of(taxCategoryWrapper.createTaxCategoryDraft()));
    }
}
