package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.commands.ProductCreateCommand;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import models.export.ProductDraftWrapper;
import models.export.ProductTypeDraftWrapper;
import play.Logger;
import play.libs.F;
import utils.JsonUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Singleton
public class ImportServiceImpl extends AbstractShopService implements ImportService {

    private static final Logger.ALogger LOG = Logger.of(ImportServiceImpl.class);

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";
    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";
    private static final Path PATH = FileSystems.getDefault().getPath("conf/data", "product-draft.json");

    private static final String CUSTOM_TYPE_KEY = "cart-frequency-key";
    private static final String CUSTOM_TYPE_LABEL = "custom type for delivery frequency";
    private static final String FREQUENCY_FIELD_NAME = "frequency";
    private static final String FREQUENCY_FIELD_LABEL = "selected frequency";

    private static final String PRODUCT_TYPE_ID_KEY = "PRODUCT-TYPE-ID";

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
        final F.Promise<ProductType> productTypePromise = createProductTypeModel();
        return productTypePromise.flatMap(productType -> {
            writeProductTypeId(productType.getId());
            final ProductDraftWrapper productDraftWrapper = JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE,
                    ProductDraftWrapper.class);
            return playJavaSphereClient().execute(ProductCreateCommand.of(productDraftWrapper.createProductDraft(productType)));
        });
    }

    private void writeProductTypeId(final String id) {
        final String data;
        try {
            data = new String(Files.readAllBytes(PATH)).replace(PRODUCT_TYPE_ID_KEY, id);
            Files.write(PATH, data.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Unable to write resource file");
        }
    }

    private F.Promise<ProductType> createProductTypeModel() {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        return playJavaSphereClient().execute(ProductTypeCreateCommand.of(productTypeDraftWrapper.createProductTypeDraft()));
    }
}
