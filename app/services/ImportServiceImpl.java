package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
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
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import models.wrapper.ProductDraftWrapper;
import models.wrapper.ProductTypeDraftWrapper;
import models.wrapper.TaxCategoryWrapper;
import play.Configuration;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    @Inject
    public ImportServiceImpl(final PlayJavaSphereClient playJavaSphereClient, final Configuration configuration) {
        super(playJavaSphereClient);
        requireNonNull(configuration);
        final Boolean importEnabled = configuration.getBoolean("fixtures.import.enabled", false);
        LOG.debug("Import enabled: {}", importEnabled);
        if(importEnabled) {
            importData();
        }
    }

    private void importData() {
        productExists().flatMap(productExits -> {
            LOG.debug("Existing Product found: {}", productExits);
            return (productExits) ?
                    F.Promise.pure(null) :
                    exportProductModel().map(product -> {
                        LOG.debug("Finished Product import");
                        return null;
                    });
        }).get(5000);
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
        customTypePromise.onRedeem(type -> LOG.debug("Created custom Type: {}", type));
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
