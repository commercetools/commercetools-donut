package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import models.export.ProductTypeDraftWrapper;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.util.*;

@Singleton
public class ExportServiceImpl extends AbstractShopService implements ExportService {

    private static final Logger.ALogger LOG = Logger.of(ExportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";
    private static final String CUSTOM_TYPE_LABEL = "custom type for delivery frequency";
    private static final String FREQUENCY_FIELD_NAME = "frequency";
    private static final String FREQUENCY_FIELD_LABEL = "selected frequency";

    @Inject
    public ExportServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Type> createCustomType() {
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
    public F.Promise<Product> createProductModel() {
        return null;
    }

    @Override
    public F.Promise<ProductType> createProductTypeModel() {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        return playJavaSphereClient().execute(ProductTypeCreateCommand.of(productTypeDraftWrapper.createProductTypeDraft()));
    }
}
