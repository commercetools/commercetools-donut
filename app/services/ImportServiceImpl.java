package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.commands.TaxCategoryCreateCommand;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import models.export.ProductDraftWrapper;
import models.export.ProductTypeDraftWrapper;
import models.export.TaxCategoryWrapper;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class ImportServiceImpl extends AbstractShopService implements ImportService {

    private static final Logger.ALogger LOG = Logger.of(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String TAX_CATEGORY_JSON_RESOURCE = "data/tax-category-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";


    private static final Path PRODUCT_PATH = FileSystems.getDefault().getPath("conf/data", "product-draft.json");
    private static final Path TAX_CATEGORY_PATH = FileSystems.getDefault().getPath("conf/data", "tax-category-draft.json");


    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";
    private static final String CUSTOM_TYPE_LABEL = "custom type for delivery frequency";
    private static final String FREQUENCY_FIELD_NAME = "frequency";
    private static final String FREQUENCY_FIELD_LABEL = "selected frequency";

    private static final String PRODUCT_TYPE_ID_KEY = "PRODUCT-TYPE-ID";
    private static final String TAX_CATEGORY_ID_KEY = "TAX_CATEGORY-ID";

    @Inject
    public ImportServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Type> exportCustomType() {
        final TypeDraft customTypeDraft = frequencyTypeDefinition();
        final F.Promise<Type> customTypePromise = playJavaSphereClient().execute(TypeCreateCommand.of(customTypeDraft));
        customTypePromise.onRedeem(type -> LOG.debug("Created custom Type: {}", type));
        return customTypePromise;
    }

    private static TypeDraft frequencyTypeDefinition() {
        final LocalizedString typeName = LocalizedString.of(Locale.ENGLISH, CUSTOM_TYPE_LABEL);
        final String cartResourceTypeId = Cart.resourceTypeId();
        final Set<String> resourceTypeIds = Collections.singleton(cartResourceTypeId);
        final List<FieldDefinition> fieldDefinitions = Arrays.asList(frequencyFieldDefinition());

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
            final ProductDraft productDraft = productDraftWrapper.createProductDraft(productType, taxCategory);
            return playJavaSphereClient().execute(ProductCreateCommand.of(productDraft));
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
